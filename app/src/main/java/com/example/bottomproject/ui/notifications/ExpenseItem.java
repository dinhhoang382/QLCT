package com.example.bottomproject.ui.notifications;

public class ExpenseItem {
    private String id;
    private String name;
    private Integer money;
    private String note;
    private String date;
    private String category;

    public ExpenseItem(){}

    public ExpenseItem(String id,String name, Integer money, String note, String date, String category){
        this.id = id;
        this.name = name;
        this.money = money;
        this.note = note;
        this.date = date;
        this.category = category;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
