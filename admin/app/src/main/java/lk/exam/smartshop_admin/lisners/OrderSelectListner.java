package lk.exam.smartshop_admin.lisners;


import lk.exam.smartshop_admin.model.Order;

public interface OrderSelectListner {
    void selectOrder(Order order);
    void confirmDelivered(Order order);
}
