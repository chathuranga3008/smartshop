package lk.exam.smartshop_customer.listener;

import lk.exam.smartshop_customer.model.Product;

public interface ProductSelectListener {
    void viewProduct(Product product);

    void addToCart(Product product);
}
