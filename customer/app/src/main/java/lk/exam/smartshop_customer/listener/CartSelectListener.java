package lk.exam.smartshop_customer.listener;

import lk.exam.smartshop_customer.model.Cart;

public interface CartSelectListener {
    void productAddQty(Cart cart);

    void productRemoveQty(Cart cart);

    void removeProduct(Cart cart);
}
