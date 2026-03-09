package lk.exam.smartshop_customer.model;

public class Cart {
    private String id;
    private String title;
    private String quantity;
    private String cost;
    private String delivery_cost;
    private String image1Id;

    public Cart() {
    }

    public Cart(String id, String title, String quantity, String cost, String delivery_cost, String image1Id) {
        this.id = id;
        this.title = title;
        this.quantity = quantity;
        this.cost = cost;
        this.delivery_cost = delivery_cost;
        this.image1Id = image1Id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getDelivery_cost() {
        return delivery_cost;
    }

    public void setDelivery_cost(String delivery_cost) {
        this.delivery_cost = delivery_cost;
    }

    public String getImage1Id() {
        return image1Id;
    }

    public void setImage1Id(String image1Id) {
        this.image1Id = image1Id;
    }
}
