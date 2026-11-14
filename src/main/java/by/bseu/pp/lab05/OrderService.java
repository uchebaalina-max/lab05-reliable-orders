package by.bseu.pp.lab05;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class OrderService {
    private static final Logger LOGGER = Logger.getLogger(OrderService.class.getName());
    private final Map<String, Order> orders = new HashMap<>();

    public Order createOrder(String id, double totalAmount) {
        try {
            OrderValidator.validateId(id);
            OrderValidator.validateAmount(totalAmount);

            if (orders.containsKey(id)) {
                throw new OrderOperationException("Заказ " + id + " уже существует");
            }

            Order order = new Order(id, totalAmount);
            orders.put(id, order);
            LOGGER.info("Создан заказ " + id + ", сумма=" + totalAmount);
            return order;
        } catch (ValidationException | OrderOperationException ex) {
            LOGGER.warning("Создание заказа " + id + " отклонено: " + ex.getMessage());
            throw ex;
        }
    }

    public Order findOrder(String id) {
        try {
            OrderValidator.validateId(id);
            Order order = getOrderOrThrow(id);
            LOGGER.fine("Найден заказ " + id + ", статус=" + order.getStatus());
            return order;
        } catch (ValidationException | OrderOperationException ex) {
            LOGGER.warning("Поиск заказа " + id + " отклонен: " + ex.getMessage());
            throw ex;
        }
    }

    public void payOrder(String id, double paymentAmount) {
        try {
            OrderValidator.validateId(id);
            OrderValidator.validateAmount(paymentAmount);
            Order order = getOrderOrThrow(id);

            if (order.getStatus() != OrderStatus.CREATED) {
                throw new OrderOperationException("Заказ " + id + " в статусе " + order.getStatus() + " нельзя оплатить");
            }

            // ВОТ ЗДЕСЬ МЫ ДОБАВИЛИ ID В СООБЩЕНИЕ!
            if (Double.compare(paymentAmount, order.getTotalAmount()) != 0) {
                throw new OrderOperationException("Заказ " + id + ": сумма оплаты " + paymentAmount + " не равна сумме заказа " + order.getTotalAmount());
            }

            order.markPaid();
            LOGGER.info("Оплачен заказ " + id + ", сумма=" + paymentAmount);

        } catch (ValidationException | OrderOperationException ex) {
            LOGGER.warning("Оплата заказа " + id + " отклонена: " + ex.getMessage());
            throw ex;
        }
    }

    public void cancelOrder(String id) {
        try {
            OrderValidator.validateId(id);
            Order order = getOrderOrThrow(id);

            if (order.getStatus() != OrderStatus.CREATED) {
                throw new OrderOperationException("Заказ " + id + " в статусе " + order.getStatus() + " нельзя отменить");
            }

            order.markCancelled();
            LOGGER.info("Отменен заказ " + id);
        } catch (ValidationException | OrderOperationException ex) {
            LOGGER.warning("Отмена заказа " + id + " отклонена: " + ex.getMessage());
            throw ex;
        }
    }

    public int getOrderCount() {
        return orders.size();
    }

    private Order getOrderOrThrow(String id) {
        Order order = orders.get(id);
        if (order == null) {
            throw new OrderOperationException("Заказ " + id + " не найден");
        }
        return order;
    }
}