package com.finexchange.finexchange.service;

import com.finexchange.finexchange.constant.TransactionConstants;
import com.finexchange.finexchange.dto.response.AllOrdersResponse;
import com.finexchange.finexchange.exception.OrderNotFoundException;
import com.finexchange.finexchange.mapper.OrderDtoMapper;
import com.finexchange.finexchange.model.Order;
import com.finexchange.finexchange.model.Wallet;
import com.finexchange.finexchange.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final BalanceService balanceService;
    private final WalletService walletService;

    public Order getOrderById(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);
    }

    public List<Order> getAlLimitOrdersByStatus(String status) {
        return orderRepository.findByStatusAndOrderTypeOrderByCreatedAtAsc(TransactionConstants.TSX_WAIT, true);
    }

    public List<Order> getAllStopOrdersByStatus(String status) {
        return orderRepository.findByStatusAndOrderTypeOrderByCreatedAtAsc(TransactionConstants.TSX_WAIT, false);
    }

    public AllOrdersResponse getAllOrderResponseAsPart(String customerId) {
        AllOrdersResponse allOrdersResponse = new AllOrdersResponse();
        List<Order> waitOrders = orderRepository.findByStatusAndCustomerIdOrderByCreatedAtAsc(TransactionConstants.TSX_WAIT, customerId);
        List<Order> cancelOrders = orderRepository.findByStatusAndCustomerIdOrderByCreatedAtAsc(TransactionConstants.TSX_CANCEL, customerId);
        allOrdersResponse.setWaiting(waitOrders.stream().map(OrderDtoMapper::mapToOrderDto).collect(Collectors.toList()));
        allOrdersResponse.setCancelled(cancelOrders.stream().map(OrderDtoMapper::mapToOrderDto).collect(Collectors.toList()));
        return allOrdersResponse;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Order saveOrder(Order order) {
        if (order == null)
            throw new OrderNotFoundException();
        return orderRepository.save(order);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void cancelOrder(String orderId) {
        Order order = getOrderById(orderId);
        Wallet wallet;

        if (order.isOrderType()) {
            wallet = walletService.getWalletEntityByCustomerIdAndCurrencyId(order.getCustomer().getId(), order.getTargetCurrency().getId());
        } else {
            wallet = walletService.getWalletEntityByCustomerIdAndCurrencyId(order.getCustomer().getId(), order.getBaseCurrency().getId());
        }
        BigDecimal newBalanceForTargetWallet = wallet.getBalance().getAmount().add(order.getBlockedBalance())
                .setScale(2, RoundingMode.HALF_UP);
        balanceService.updateBalanceWithWalletEntity(wallet, newBalanceForTargetWallet);

        order.setStatus(TransactionConstants.TSX_CANCEL);
        saveOrder(order);
    }
}
