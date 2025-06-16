# 📦 Inventra - Inventory Management & Billing App

**Inventra** is a modern Inventory Management & Billing Android application built using **Java**, **Retrofit**, **MySQL**, and **PHP APIs**. It helps businesses efficiently manage products, staff, stock, and sales from a mobile device.

---

## 🚀 Features

### 🔍 Product Management
- Add new products with name, category, price, description, and image
- Add stock to existing products
- View product list in a clean, card-based layout
- Search products using:
  - Text-based search
  - Voice search (via mic input)
  - Barcode scanner (ML Kit-powered)

### 👥 Staff Management
- Add staff members with name and email
- Remove staff with confirmation dialogs
- All data managed locally with **SQLite** and synced with **MySQL**

### 🛒 Cart & Billing
- Add products to cart with quantity control
- Real-time cart total and swipe-to-remove functionality
- Generate invoice with customer details and purchase summary
- View recent invoices with status (Paid/Pending)

### 📊 Dashboard & Analytics
- View total products, stock quantities, and percentage changes from last month
- Pie chart for category-wise product distribution
- Bar chart for sales trends (daily, monthly, yearly)

### 📦 Categories
- Add and manage product categories
- Category-wise organization of products

### 📷 Smart Input Options
- Barcode scanning using Google ML Kit
- Voice search with Android Speech API
- Rich product descriptions using HTML

### ⚠️ Low Stock Notification
- Periodic background check for low-stock items using **WorkManager**
- Notifies user when stock goes below a threshold

---

## 🛠️ Tech Stack

- **Language**: Java
- **Backend**: PHP + MySQL
- **API Communication**: Retrofit
- **Local Storage**: SQLite
- **Charts & UI**: MPAndroidChart, Material Design Components
- **Barcode Scanning**: Google ML Kit
- **Voice Recognition**: Android SpeechRecognizer
- **Task Scheduling**: WorkManager

### 📁 Database File (database name : inventra)
- **File**: inventra.sql

### 📁 Folder Structure (Important Parts)
```
Inventra/
├── app/
│   ├── java/com/example/inventra/
│   │   ├── MainActivity.java          # Main Acitivty of Application
│   │   ├── DBConnect/                 # Retrofit API client & service interface
│   │   ├── admin/                     # All admin features (inventory, staff)
│   │   │   ├── AddInventory/          # Folder that contains adapters and model classes
│   │   │   ├── BarcodeScan/           # Folder that contains adapters and model classes for barcode scanning functionality
│   │   │   └── Other Folders          
│   └── res/layout/                    # XML layouts for fragments & activities
└── README.md

Inventra (PHP APIs)/
├── cart/
│   ├── add_to_cart.php
│   └── other files
├── category/
│   ├── add_category.php
│   └── get_all_category.php
├── img/
├── login/
│   └── login.php
├── notification/
│   └── get_low_stock_products.php
├── product/
│   ├── add_new_product.php
│   └── other files
├── profile/
├── sales/
│   ├── get_recent_sales.php
│   └── other files
├── staff/
│   ├── add_staff.php
│   └── other files
├── db_config.php
├── get_dashboard_data.php
├── get_sales_amount.php
├── get_total_products.php
├── get_yearly_sales.php
└── getProductCategories.php

```


## 📸 Screenshots
![image](https://github.com/user-attachments/assets/a9803cb6-1a09-46f9-a441-33a20ee0b9db)
![image](https://github.com/user-attachments/assets/66d1ec0b-30c4-4d3e-9afd-1f7cc6dcd152)
![image](https://github.com/user-attachments/assets/eddea403-2a3c-4cb8-b34f-518ff67d0e7d)
![image](https://github.com/user-attachments/assets/85e94cc0-d2be-4f37-aa79-c34039204017)
![image](https://github.com/user-attachments/assets/07c45c0b-0473-4cc6-991b-c4381bac50db)
![image](https://github.com/user-attachments/assets/ddcb5db8-8e14-4d54-90ab-b0f021020a2a)
![image](https://github.com/user-attachments/assets/b0cc1029-e896-4e32-b933-378ed290f2d4)
![image](https://github.com/user-attachments/assets/31171b4b-c0d9-44db-a294-14c86de68840)
![image](https://github.com/user-attachments/assets/f14f68c8-23a7-4f47-8280-e51c35895721)
![image](https://github.com/user-attachments/assets/a9ccc152-851d-4e0a-a6d8-63fe89b29d47)
![image](https://github.com/user-attachments/assets/de8849eb-96b0-49ef-ae65-0563941ee90a)
![image](https://github.com/user-attachments/assets/a35b2a9f-3b54-4440-bdc8-51259d090d02)
![image](https://github.com/user-attachments/assets/f2f3e518-9a33-46a7-90a2-0e8436e7224c)


---

## ⚙️ Setup & Installation

1. Clone the repo:
   ```bash
   git clone https://github.com/prakash0067/Inventra.git
