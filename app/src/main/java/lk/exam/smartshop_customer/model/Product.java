package lk.exam.smartshop_customer.model;

public class Product {
    private String id;
    private String title;
    private String category;
    private String variation;
    private String quantity;
    private String price;
    private String delivery_cost;
    private String description;
    private String image1Id;
    private String image2Id;
    private String image3Id;

    public Product() {
    }

    public Product(String id, String title, String category, String variation, String quantity, String price, String delivery_cost, String description, String image1Id, String image2Id, String image3Id) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.variation = variation;
        this.quantity = quantity;
        this.price = price;
        this.delivery_cost = delivery_cost;
        this.description = description;
        this.image1Id = image1Id;
        this.image2Id = image2Id;
        this.image3Id = image3Id;
    }

    public Product(String id, String title, String quantity, String price, String image1Id) {
        this.id = id;
        this.title = title;
        this.quantity = quantity;
        this.price = price;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getVariation() {
        return variation;
    }

    public void setVariation(String variation) {
        this.variation = variation;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDelivery_cost() {
        return delivery_cost;
    }

    public void setDelivery_cost(String delivery_cost) {
        this.delivery_cost = delivery_cost;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage1Id() {
        return image1Id;
    }

    public void setImage1Id(String image1Id) {
        this.image1Id = image1Id;
    }

    public String getImage2Id() {
        return image2Id;
    }

    public void setImage2Id(String image2Id) {
        this.image2Id = image2Id;
    }

    public String getImage3Id() {
        return image3Id;
    }

    public void setImage3Id(String image3Id) {
        this.image3Id = image3Id;
    }
}
