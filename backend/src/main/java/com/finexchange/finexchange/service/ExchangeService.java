package com.finexchange.finexchange.service;

import com.finexchange.finexchange.cache.UserContextCache;
import com.finexchange.finexchange.constant.TransactionConstants;
import com.finexchange.finexchange.dto.request.ExchangeOrderRequest;
import com.finexchange.finexchange.dto.tcmbapi.TcmbExchangeRate;
import com.finexchange.finexchange.dto.tcmbapi.TcmbExchangeRateResponse;
import com.finexchange.finexchange.enums.ExchangeCode;
import com.finexchange.finexchange.exception.*;
import com.finexchange.finexchange.model.*;
import com.finexchange.finexchange.repository.ExchangeRateRepository;
import com.finexchange.finexchange.util.DateUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExchangeService {
    private final ExchangeRateRepository exchangeRateRepository;
    private final CurrencyService currencyService;
    private final WalletService walletService;
    private final UserContextCache userContextCache;
    private final CustomerService customerService;
    private final BalanceService balanceService;
    private final TransactionService transactionService;
    private final OrderService orderService;
    private final TcmbExchangeRateService tcmbExchangeRateService;

    public ExchangeRate getCurrentExchangeRateByBaseCurrencyIdAndTargetCurrencyId(String baseCurrencyId, String targetCurrencyId) {
        return exchangeRateRepository.findTopByBaseCurrencyIdAndTargetCurrencyIdOrderByCreatedAtDesc(baseCurrencyId, targetCurrencyId)
                .orElseThrow(ExchangeRateNotFoundException::new);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public ExchangeRate addExchangeRate(TcmbExchangeRate tcmbExchangeRate, String baseCurrency, String targetCurrency) {
        Currency baseCurrencyEntity = currencyService.getCurrencyByCode(baseCurrency);
        Currency targetCurrencyEntity = currencyService.getCurrencyByCode(targetCurrency);
        boolean existControl = exchangeRateRepository
                .existsByBaseCurrencyIdAndTargetCurrencyIdAndDataDate(baseCurrencyEntity.getId(), targetCurrencyEntity.getId(), tcmbExchangeRate.getDataDate());
        if (!existControl) {
            ExchangeRate exchangeRate = ExchangeRate.builder()
                    .exchangeCode(tcmbExchangeRate.getAssetCode())
                    .ask(tcmbExchangeRate.getAsk())
                    .bid(tcmbExchangeRate.getBid())
                    .baseCurrency(baseCurrencyEntity)
                    .targetCurrency(targetCurrencyEntity)
                    .dataDate(tcmbExchangeRate.getDataDate())
                    .createdAt(LocalDateTime.now())
                    .build();

            ExchangeRate rate = exchangeRateRepository.save(exchangeRate);
            System.out.println(rate);
            return rate;
        } else return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void createExchangeOrder(ExchangeOrderRequest orderRequest) {
        if (orderRequest.getTransactionType() == 0) {
            //PİYASA EMRİ İŞLER
            processMarketOrder(orderRequest);
        } else if (orderRequest.getTransactionType() == 1) {
            //LİMİT EMİR YARATIR
            createLimitOrder(orderRequest);
        } else if (orderRequest.getTransactionType() == 2) {
            //STOP EMİR YARATIR
            createStopOrder(orderRequest);
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void processMarketOrder(ExchangeOrderRequest orderRequest) {

        //spring validasyon ile kontrol yapılarak zaten işlemler yaratılıyor ama ekstra güvenlik için null veya negatif miktar kontolü buraya da eklendi
        if (orderRequest.getAmount() == null || orderRequest.getAmount().compareTo(BigDecimal.ZERO) < 0)
            throw new IncorrectBalanceEntryException();

        //adına işlem yapılacak mşteri
        Customer customer = customerService.getCustomerEntityById(orderRequest.getCustomerId());

        //yetkili kişinin müşteri adına yetkisi var mı kontrolü
        if (!userContextCache.getCurrentUser().getId().equals(customer.getUser().getId()))
            throw new InsufficientAuthorityException();

        //ana para birimi
        Currency baseCurrency = currencyService.getCurrencyByCode(orderRequest.getBaseCurrency());

        //ikincil para birimi
        Currency targetCurrency = currencyService.getCurrencyByCode(orderRequest.getTargetCurrency());

        //ana para birimine ait cüzdan (bakiye entity varlığını kontrol ederek döner)
        Wallet baseWallet = walletService.getWalletEntityByCustomerIdAndCurrencyId(customer.getId(), baseCurrency.getId());

        //ikincil para birimine ait cüzdan (bakiye entity varlığını kontrol ederek döner)
        Wallet targetWallet = walletService.getWalletEntityByCustomerIdAndCurrencyId(customer.getId(), targetCurrency.getId());

        //iki para birimi için anlık kur
        ExchangeRate exchangeRate = getCurrentExchangeRateByBaseCurrencyIdAndTargetCurrencyId(baseCurrency.getId(), targetCurrency.getId());

        //ana para cinsinden satış emri
        if (orderRequest.isOrderType()) {

            //bankanın alış fiyatı üzerinden virgülden sonra 2 haneliye yuvarlayarak alınacak ikincil para cinsi hesaplanır
            BigDecimal quantity = exchangeRate.getBid().multiply(orderRequest.getAmount())
                    .setScale(2, RoundingMode.HALF_UP);

            int comparison = orderRequest.getAmount().compareTo(baseWallet.getBalance().getAmount());

            //Müşterinin cüzdanındaki ana para cinsindeki bakiye yeterli
            if (comparison < 0 || comparison == 0) {

                //ana para cüzdanında satılacak olan miktar çıkartılır
                BigDecimal newBalanceForBaseWallet = baseWallet.getBalance().getAmount().subtract(orderRequest.getAmount())
                        .setScale(2, RoundingMode.HALF_UP);
                balanceService.updateBalanceWithWalletEntity(baseWallet, newBalanceForBaseWallet);

                //satılacak miktara karşılık gelen ikincil para cüzdanına eklenir
                BigDecimal newBalanceForTargetWallet = targetWallet.getBalance().getAmount().add(quantity)
                        .setScale(2, RoundingMode.HALF_UP);
                balanceService.updateBalanceWithWalletEntity(targetWallet, newBalanceForTargetWallet);

                Transaction transaction = Transaction.builder()
                        .customer(customer)
                        .currencyFrom(baseCurrency)
                        .currencyTo(targetCurrency)
                        .amountFrom(orderRequest.getAmount())
                        .amountTo(quantity)
                        .transactionType(TransactionConstants.TSX_SELL)
                        .status(TransactionConstants.TSX_SUCCESS)
                        .currencyPrice(exchangeRate.getBid())
                        .createdAt(LocalDateTime.now())
                        .build();

                transactionService.saveTransaction(transaction);
            } else {
                //Müşterinin cüzdanındaki bakiye yetersiz
                throw new InsufficientBalanceException();
            }
        } else {
            //ana para cinsinden alış emri

            //bankanın satış fiyatı üzerinden virgülden sonra 2 haneliye yuvarlayarak satılacak ikincil para cinsi miktarı belirnelir
            BigDecimal quantity = exchangeRate.getAsk().multiply(orderRequest.getAmount())
                    .setScale(2, RoundingMode.HALF_UP);

            int comparison = quantity.compareTo(targetWallet.getBalance().getAmount());

            //Müşterinin cüzdanındaki ikincil para cinsideki bakiye yeterli
            if (comparison < 0 || comparison == 0) {

                //ana para cüzdanına alınan miktar eklenir
                BigDecimal newBalanceForBaseWallet = baseWallet.getBalance().getAmount().add(orderRequest.getAmount())
                        .setScale(2, RoundingMode.HALF_UP);
                balanceService.updateBalanceWithWalletEntity(baseWallet, newBalanceForBaseWallet);

                //ikincil para cüzdanından hesaplanan tutar çıkarılır
                BigDecimal newBalanceForTargetWallet = targetWallet.getBalance().getAmount().subtract(quantity)
                        .setScale(2, RoundingMode.HALF_UP);
                balanceService.updateBalanceWithWalletEntity(targetWallet, newBalanceForTargetWallet);

                Transaction transaction = Transaction.builder()
                        .customer(customer)
                        .currencyFrom(baseCurrency)
                        .currencyTo(targetCurrency)
                        .amountFrom(orderRequest.getAmount())
                        .amountTo(quantity)
                        .transactionType(TransactionConstants.TSX_BUY)
                        .status(TransactionConstants.TSX_SUCCESS)
                        .currencyPrice(exchangeRate.getAsk())
                        .createdAt(LocalDateTime.now())
                        .build();

                transactionService.saveTransaction(transaction);
            } else {
                //Müşterinin cüzdanındaki bakiye yetersiz
                throw new InsufficientBalanceException();
            }
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void createLimitOrder(ExchangeOrderRequest orderRequest) {
        //adına işlem yapılacak mşteri
        Customer customer = customerService.getCustomerEntityById(orderRequest.getCustomerId());

        //yetkili kişinin müşteri adına yetkisi var mı kontrolü
        if (!userContextCache.getCurrentUser().getId().equals(customer.getUser().getId()))
            throw new InsufficientAuthorityException();

        //ana para birimi
        Currency baseCurrency = currencyService.getCurrencyByCode(orderRequest.getBaseCurrency());

        //ikincil para birimi
        Currency targetCurrency = currencyService.getCurrencyByCode(orderRequest.getTargetCurrency());

        //ana para birimine ait cüzdan (bakiye entity varlığını kontrol ederek döner)
        Wallet baseWallet = walletService.getWalletEntityByCustomerIdAndCurrencyId(customer.getId(), baseCurrency.getId());

        //ikincil para birimine ait cüzdan (bakiye entity varlığını kontrol ederek döner)
        Wallet targetWallet = walletService.getWalletEntityByCustomerIdAndCurrencyId(customer.getId(), targetCurrency.getId());

        //iki para birimi için anlık kur
        ExchangeRate exchangeRate = getCurrentExchangeRateByBaseCurrencyIdAndTargetCurrencyId(baseCurrency.getId(), targetCurrency.getId());

        //limit emir beklenen fiyat piyasa fiyatından yüksek veya eşit olursa hata reponse döner
        int limitPriceComp = orderRequest.getExpectedPrice().compareTo(exchangeRate.getAsk());
        if (limitPriceComp >= 0) throw new LimitOrderPriceException();


        //bankanın satış fiyatı üzerinden virgülden sonra 2 haneliye yuvarlayarak satılacak ikincil para cinsi miktarı belirnelir
        BigDecimal quantity = orderRequest.getExpectedPrice().multiply(orderRequest.getAmount())
                .setScale(2, RoundingMode.HALF_UP);

        int comparison = quantity.compareTo(targetWallet.getBalance().getAmount());

        //Müşterinin cüzdanındaki ikincil para cinsideki bakiye yeterli
        if (comparison < 0 || comparison == 0) {

            //ikincil para cüzdanından hesaplanan tutar çıkarılır, aşağıda da bloke olarak bekletilir
            BigDecimal newBalanceForTargetWallet = targetWallet.getBalance().getAmount().subtract(quantity)
                    .setScale(2, RoundingMode.HALF_UP);
            balanceService.updateBalanceWithWalletEntity(targetWallet, newBalanceForTargetWallet);

            Order order = Order.builder()
                    .customer(customer)
                    .orderType(true)
                    .baseCurrency(baseCurrency)
                    .targetCurrency(targetCurrency)
                    .amount(orderRequest.getAmount())
                    .expectedPrice(orderRequest.getExpectedPrice())
                    .blockedBalance(quantity)
                    .status(TransactionConstants.TSX_WAIT)
                    .createdAt(LocalDateTime.now())
                    .build();

            orderService.saveOrder(order);
        } else {
            //Müşterinin cüzdanındaki bakiye yetersiz
            throw new InsufficientBalanceException();
        }

    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void createStopOrder(ExchangeOrderRequest orderRequest) {
        //adına işlem yapılacak mşteri
        Customer customer = customerService.getCustomerEntityById(orderRequest.getCustomerId());

        //yetkili kişinin müşteri adına yetkisi var mı kontrolü
        if (!userContextCache.getCurrentUser().getId().equals(customer.getUser().getId()))
            throw new InsufficientAuthorityException();

        //ana para birimi
        Currency baseCurrency = currencyService.getCurrencyByCode(orderRequest.getBaseCurrency());

        //ikincil para birimi
        Currency targetCurrency = currencyService.getCurrencyByCode(orderRequest.getTargetCurrency());

        //ana para birimine ait cüzdan (bakiye entity varlığını kontrol ederek döner)
        Wallet baseWallet = walletService.getWalletEntityByCustomerIdAndCurrencyId(customer.getId(), baseCurrency.getId());

        //iki para birimi için anlık kur
        ExchangeRate exchangeRate = getCurrentExchangeRateByBaseCurrencyIdAndTargetCurrencyId(baseCurrency.getId(), targetCurrency.getId());

        //stop emir beklenen fiyat piyasa fiyatından yüksek veya eşit olursa hata reponse döner
        int stopEmirComp = orderRequest.getExpectedPrice().compareTo(exchangeRate.getBid());
        if (stopEmirComp >= 0) throw new StopOrderPriceException();

        //stop emir için döviz miktarı
        BigDecimal quantity = orderRequest.getAmount();

        int comparison = quantity.compareTo(baseWallet.getBalance().getAmount());

        //Müşterinin cüzdanındaki ana para cinsideki bakiye yeterli
        if (comparison < 0 || comparison == 0) {

            //ana para cüzdanından hesaplanan tutar çıkarılır, aşağıda da bloke olarak bekletilir
            BigDecimal newBalanceForBaseWallet = baseWallet.getBalance().getAmount().subtract(quantity)
                    .setScale(2, RoundingMode.HALF_UP);
            balanceService.updateBalanceWithWalletEntity(baseWallet, newBalanceForBaseWallet);

            Order order = Order.builder()
                    .customer(customer)
                    .orderType(false)
                    .baseCurrency(baseCurrency)
                    .targetCurrency(targetCurrency)
                    .amount(orderRequest.getAmount())
                    .expectedPrice(orderRequest.getExpectedPrice())
                    .blockedBalance(quantity)
                    .status(TransactionConstants.TSX_WAIT)
                    .createdAt(LocalDateTime.now())
                    .build();

            orderService.saveOrder(order);
        } else {
            //Müşterinin cüzdanındaki bakiye yetersiz
            throw new InsufficientBalanceException();
        }

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void limitOrdersControl() {
        List<Order> orderList = orderService.getAlLimitOrdersByStatus(TransactionConstants.TSX_WAIT);

        for (int i = 0; i < orderList.size(); i++) {

            Order order = orderList.get(i);

            //adına işlem yapılacak mşteri
            Customer customer = order.getCustomer();

            //ana para birimi
            Currency baseCurrency = order.getBaseCurrency();

            //ikincil para birimi
            Currency targetCurrency = order.getTargetCurrency();

            //ana para birimine ait cüzdan (bakiye entity varlığını kontrol ederek döner)
            Wallet baseWallet = walletService.getWalletEntityByCustomerIdAndCurrencyId(customer.getId(), baseCurrency.getId());

            //ikincil para birimine ait cüzdan (bakiye entity varlığını kontrol ederek döner)
            Wallet targetWallet = walletService.getWalletEntityByCustomerIdAndCurrencyId(customer.getId(), targetCurrency.getId());

            //iki para birimi için anlık kur
            ExchangeRate exchangeRate = getCurrentExchangeRateByBaseCurrencyIdAndTargetCurrencyId(baseCurrency.getId(), targetCurrency.getId());

            //ana para cinsinden alış emri

            //bankanın satış fiyatı üzerinden virgülden sonra 2 haneliye yuvarlayarak satılacak ikincil para cinsi miktarı belirnelir
            BigDecimal quantity = exchangeRate.getAsk().multiply(order.getAmount())
                    .setScale(2, RoundingMode.HALF_UP);

            int comparison = quantity.compareTo(order.getAmount().multiply(order.getExpectedPrice()));

            //Müşterinin istediği fiyata düşme veya daha azına ulaşma kontrolü
            if (comparison < 0 || comparison == 0) {

                //bloke edilmiş bakiyeden hesaplanan para çıkar
                BigDecimal newBalanceForBlockedBalance = order.getBlockedBalance().subtract(quantity)
                        .setScale(2, RoundingMode.HALF_UP);

                //ana para cüzdanına alınan miktar eklenir
                BigDecimal newBalanceForBaseWallet = baseWallet.getBalance().getAmount().add(order.getAmount())
                        .setScale(2, RoundingMode.HALF_UP);
                balanceService.updateBalanceWithWalletEntity(baseWallet, newBalanceForBaseWallet);

                //ikincil para cüzdanına limit emirdeki bloke bakiyeden artmış olan aktarılır
                if (newBalanceForBlockedBalance.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal newBalanceForTargetWallet = targetWallet.getBalance().getAmount().add(newBalanceForBlockedBalance)
                            .setScale(2, RoundingMode.HALF_UP);
                    balanceService.updateBalanceWithWalletEntity(targetWallet, newBalanceForTargetWallet);
                }

                Transaction transaction = Transaction.builder()
                        .customer(customer)
                        .currencyFrom(baseCurrency)
                        .currencyTo(targetCurrency)
                        .amountFrom(order.getAmount())
                        .amountTo(quantity)
                        .transactionType(TransactionConstants.TSX_BUY)
                        .status(TransactionConstants.TSX_SUCCESS)
                        .currencyPrice(exchangeRate.getAsk())
                        .createdAt(LocalDateTime.now())
                        .build();

                transactionService.saveTransaction(transaction);
            } else {
                //Müşterinin cüzdanındaki bakiye yetersiz

                //limit emir 5 günlük süreyi doldurmuşsa
                Duration duration = Duration.between(order.getCreatedAt(), LocalDateTime.now());
                if (duration.getSeconds() > 432000) {
                    order.setStatus(TransactionConstants.TSX_CANCEL);
                    orderService.saveOrder(order);

                    //ikincil para cüzdanına limit emirdeki bloke bakiye aktarılır
                    BigDecimal newBalanceForTargetWallet = targetWallet.getBalance().getAmount().add(order.getBlockedBalance())
                            .setScale(2, RoundingMode.HALF_UP);
                    balanceService.updateBalanceWithWalletEntity(targetWallet, newBalanceForTargetWallet);
                }
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void stopOrdersControl() {
        List<Order> orderList = orderService.getAllStopOrdersByStatus(TransactionConstants.TSX_WAIT);

        for (int i = 0; i < orderList.size(); i++) {

            Order order = orderList.get(i);

            //adına işlem yapılacak mşteri
            Customer customer = order.getCustomer();

            //ana para birimi
            Currency baseCurrency = order.getBaseCurrency();

            //ikincil para birimi
            Currency targetCurrency = order.getTargetCurrency();

            //ana para birimine ait cüzdan (bakiye entity varlığını kontrol ederek döner)
            Wallet baseWallet = walletService.getWalletEntityByCustomerIdAndCurrencyId(customer.getId(), baseCurrency.getId());

            //ikincil para birimine ait cüzdan (bakiye entity varlığını kontrol ederek döner)
            Wallet targetWallet = walletService.getWalletEntityByCustomerIdAndCurrencyId(customer.getId(), targetCurrency.getId());

            //iki para birimi için anlık kur
            ExchangeRate exchangeRate = getCurrentExchangeRateByBaseCurrencyIdAndTargetCurrencyId(baseCurrency.getId(), targetCurrency.getId());

            int comparison = exchangeRate.getBid().compareTo(order.getExpectedPrice());

            //Müşterinin emir girdiği fiyata bankanın alış fiyatının düşme veya daha azına ulaşma kontrolü
            if (comparison < 0 || comparison == 0) {


                //bankanın alış fiyatı üzerinden virgülden sonra 2 haneliye yuvarlayarak alınacak ikincil para cinsi hesaplanır
                BigDecimal quantity = exchangeRate.getBid().multiply(order.getAmount())
                        .setScale(2, RoundingMode.HALF_UP);

                //ikincil para cüzdanına tutar eklenir
                BigDecimal newBalanceForTargetWallet = targetWallet.getBalance().getAmount().add(quantity)
                        .setScale(2, RoundingMode.HALF_UP);
                balanceService.updateBalanceWithWalletEntity(targetWallet, newBalanceForTargetWallet);

                Transaction transaction = Transaction.builder()
                        .customer(customer)
                        .currencyFrom(baseCurrency)
                        .currencyTo(targetCurrency)
                        .amountFrom(order.getAmount())
                        .amountTo(quantity)
                        .transactionType(TransactionConstants.TSX_BUY)
                        .status(TransactionConstants.TSX_SUCCESS)
                        .currencyPrice(exchangeRate.getAsk())
                        .createdAt(LocalDateTime.now())
                        .build();

                transactionService.saveTransaction(transaction);
            } else {
                //Müşterinin cüzdanındaki bakiye yetersiz

                //limit emir 5 günlük süreyi doldurmuşsa
                Duration duration = Duration.between(order.getCreatedAt(), LocalDateTime.now());
                if (duration.getSeconds() > 432000) {
                    order.setStatus(TransactionConstants.TSX_CANCEL);
                    orderService.saveOrder(order);

                    //ikincil para cüzdanına limit emirdeki bloke bakiye aktarılır
                    BigDecimal newBalanceForTargetWallet = targetWallet.getBalance().getAmount().add(order.getBlockedBalance())
                            .setScale(2, RoundingMode.HALF_UP);
                    balanceService.updateBalanceWithWalletEntity(targetWallet, newBalanceForTargetWallet);
                }
            }
        }
    }


    @PostConstruct
    @Transactional(propagation = Propagation.REQUIRED)
    public void init() {
        if (exchangeRateRepository.count() == 0) {
            List<String> last10WeekdaysIncludingTodayList = DateUtils.getLast10WeekdaysIncludingToday();
            for (int i = 0; i < last10WeekdaysIncludingTodayList.size(); i++) {
                for (ExchangeCode code : ExchangeCode.values()) {
                    TcmbExchangeRateResponse exchangeRateResponse =
                            tcmbExchangeRateService.getTcmbExchangeRateResponse(last10WeekdaysIncludingTodayList.get(i), code.toString());

                    if (exchangeRateResponse != null
                            && exchangeRateResponse.getResult() != null
                            && exchangeRateResponse.getResult().getData() != null
                            && exchangeRateResponse.getResult().getData().getTcmbExchangeRateList() != null
                            && !exchangeRateResponse.getResult().getData().getTcmbExchangeRateList().isEmpty()
                    ) {

                        TcmbExchangeRate tcmbExchangeRate = exchangeRateResponse.getResult().getData().getTcmbExchangeRateList().get(0);
                        addExchangeRate(tcmbExchangeRate, code.getBaseCurrency(), code.getTargetCurrency());
                    }
                }
            }
        }
    }


}
