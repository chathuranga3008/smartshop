package lk.exam.smartshop_admin.lisners;


import lk.exam.smartshop_admin.model.Product;

public interface ProductListener {

    void deleteProduct(Product product);
    void updateProduct(Product product);
}
