FarmMate - Natural Farming Marketplace

Video Link- https://drive.google.com/file/d/17ixczBwdyMGfS2rTNOdENNsdwp9zx4lD/view?usp=sharing

Overview

FarmMate is a Natural Farming Marketplace that connects farmers and consumers directly. The platform ensures farmer verification, product authenticity, and consumer trust by integrating a structured registration and approval system.

Features

Farmer Registration & Approval System

Farmers register via a mobile application.

Admin receives an email notification.

Admin can approve or reject requests based on certification.

Farmers get notified via email upon approval/rejection.

Product Management

Verified farmers can list products with pricing and stock details.

Consumers can browse and purchase products.

Order & Cart Management

Consumers can add products to the cart and place orders.

Admin and farmers can manage order statuses.

QR Code Traceability

Approved farmers receive a unique QR code.

Consumers can scan QR codes to verify product authenticity.

Tech Stack

Android (Java) - Mobile App Development

PHP & MySQL - Backend & Database

Firebase Authentication - OTP-based Registration/Login

PHPMailer - Email Notifications

MermaidJS - Diagrams & Flowcharts

Installation Guide

1️⃣ Setup the Database

Import farmmate.sql into MySQL.

Ensure the following tables exist:

request_farmer

approved_farmer

rejected_farmer

products

orders

cart

users

farmmate_hander (Admin table)

2️⃣ Setup the PHP Backend (API)

Configure conn.php with your database credentials.

Update PHPMailer settings with your SMTP credentials.

Place all PHP files inside the server root (htdocs if using XAMPP).

3️⃣ Setup the Android App

Update Base URL in API calls to match your server address.

Configure Firebase Authentication for OTP-based login.

Compile and run the project in Android Studio.



Contributors

Team Name: The Spartens
Team Members:VAGHAMASHI JAY, HARIYANI YAGNIK, VALA YUVRAJ, RATHOD TUSHAR 
