-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 16, 2025 at 04:45 PM
-- Server version: 10.4.27-MariaDB
-- PHP Version: 8.1.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `inventra`
--

-- --------------------------------------------------------

--
-- Table structure for table `cart`
--

CREATE TABLE `cart` (
  `cart_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  `added_at` datetime NOT NULL DEFAULT current_timestamp(),
  `added_by` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `cart`
--

INSERT INTO `cart` (`cart_id`, `product_id`, `quantity`, `added_at`, `added_by`) VALUES
(65, 34, 1, '2025-06-15 22:54:04', 1),
(66, 35, 2, '2025-06-15 22:54:10', 1);

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

CREATE TABLE `categories` (
  `category_id` int(11) NOT NULL,
  `category_name` varchar(255) NOT NULL,
  `description` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`category_id`, `category_name`, `description`, `created_at`) VALUES
(4, 'Electronics', 'Electric Items', '2025-05-02 16:55:12'),
(5, 'Shoes', 'Shoes items', '2025-05-02 16:57:53'),
(6, 'Clothing', 'clothing items', '2025-05-02 18:27:32'),
(7, 'Home & Kitchen', 'Essentials for home & cooking', '2025-05-03 18:23:44'),
(8, 'Sports', 'Gear for sports', '2025-05-03 18:23:58'),
(9, 'Furniture', 'Furniture items', '2025-05-03 18:24:15');

-- --------------------------------------------------------

--
-- Table structure for table `payments`
--

CREATE TABLE `payments` (
  `payment_id` int(11) NOT NULL,
  `sale_id` int(11) DEFAULT NULL,
  `amount` decimal(10,2) NOT NULL,
  `payment_date` timestamp NOT NULL DEFAULT current_timestamp(),
  `payment_method` enum('cash','UPI','credit','debit') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `products`
--

CREATE TABLE `products` (
  `product_id` int(11) NOT NULL,
  `product_name` varchar(255) NOT NULL,
  `category_id` int(11) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `price` decimal(10,2) NOT NULL,
  `quantity` int(11) NOT NULL,
  `barcode` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `image_path` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `products`
--

INSERT INTO `products` (`product_id`, `product_name`, `category_id`, `description`, `price`, `quantity`, `barcode`, `created_at`, `image_path`) VALUES
(34, 'Apple iPhone 16 Pro (Natural Titanium, 128 GB)', 4, 'iPhone 16 Pro. Built for Apple Intelligence. Featuring a stunning titanium design. Camera Control. 4K 120 fps Dolby Vision. And A18 Pro chip.\n\nOther Display Features\n	\nDynamic Island, Always On Display, ProMotion Technology with Adaptive Refresh Rates Upto 120Hz, HDR Display, True Tone, Wide Colour (P3), Haptic Touch, Contrast Ratio: 2,000,000:1 (Typical), 1,000 nits Max Brightness (Typical), 1,600 nits Peak Brightness (HDR), 2,000 nits Peak Brightness (Outdoor), Fingerprint Resistant Oleophobic Coating, Support for Display of Multiple Languages and Characters Simultaneously', '109900.00', 38, '1234567890', '2025-06-15 14:06:05', 'product_34_1750000603.jpg'),
(35, 'Ambrane 10000 mAh 22.5 W Wireless With MagSafe Power Bank', 4, 'Introducing the Aerosync Snap, a powerful 10000 mAh MagSafe power bank designed for seamless charging on the go. With a maximum output of 22.5W and multiple charging ports, including a 22W Type-C, 22.5W USB-A, and 15W MagSafe output, this power bank offers versatile and efficient power delivery. It supports PD 3.0, QC 3.0, VOOC, and PPS protocols, ensuring compatibility with a wide range of devices. The Snap features a premium rubberized coating and 5 LED indicators for easy status monitoring. Compatible with 12 and above for MagSafe, as well as all 8 and above models and Android phones through wired and wireless connections, this power bank is your reliable charging companion.', '1349.00', 147, '1234567891', '2025-06-15 14:07:39', 'product_35_1750000614.jpg'),
(36, 'ZEBRONICS Jaguar Wireless Ambidextrous Optical Mouse Wireless Mouse, High Precision, 4 Buttons, Plug & Play Ambidextrous  (2.4GHz Wireless, Black)', 4, 'Model Name: Jaguar\n\nSystem Requirements: Windows\nForm Factor: Ambidextrous\nSales Package: 1 Unit Mouse, Nano Receiver\nCompatible Devices: Laptop, Computer\nColor: Black', '449.00', 199, '1234567892', '2025-06-15 14:10:27', 'product_36_1750000633.jpg'),
(37, 'SpinBot Clutch GT900 with 7200 DPI Gaming Sensor and 8 Programmable Buttons RGB Wired Ambidextrous Optical Gaming Mouse  (USB 2.0, Black)', 4, 'Precision Tracking: With an ultra-high DPI of 7200, the SpinBot Clutch GT900 Gaming mouse offers pinpoint accuracy and responsiveness for seamless gameplay. Customisable Buttons: Equipped with programmable buttons, you can assign macros and hotkeys for enhanced control and personalised gaming experience. Vibrant Illumination: The RGB lighting system adds a stylish touch and allows you to customise the mouse\'s appearance to match your gaming setup. Ergonomic Design: Crafted with a contoured shape, this gaming mouse ensures a comfortable grip and extended gaming sessions without fatigue. Durable Construction: Built with high-quality materials, the SpinBot Clutch GT900 is designed to withstand intense gaming sessions and provide long-lasting performance.', '1499.00', 99, '1234567894', '2025-06-15 14:11:32', 'product_37_1750000647.jpg'),
(38, 'boAt 100 Wired  (Black, In the Ear)', 4, 'If you are looking for a stylish pair of wired earphones, you should consider the boAt BassHeads 100 earphones. The earbuds of this pair of earphones come with a unique design that is inspired by the hawk and looks extremely trendy. The boAt BassHeads 100 earphones are also lightweight and can be worn easily for long durations.', '349.00', 124, '1234567895', '2025-06-15 14:12:41', 'product_38_1750000656.jpg'),
(39, 'SIGNATURE Sports shoes, Walking, Lightweight, Trekking, Sneakers, Stylish Sneakers For Men  (White, Grey , 10)', 5, 'Versatile Performance\n\nThanks to their adaptable design, these men\'s shoes effortlessly transition from casual walks to intense workouts. Also, if you\'re hitting the gym for a rigorous workout session, exploring nature trails on a weekend hike, or simply running errands around town, these shoes provide the support and comfort you need to tackle every step of your day with confidence and ease.\n\nSupportive Sole\n\nCourtesy of their TPR sole, these shoes offer support and traction on a variety of surfaces. The practical cushioning system and quality midsole ensure cloud-like comfort, absorbing impact and reducing stress on your joints during activities, like running and walking. Additionally, the round tip and unique pattern add a stylish touch to your footwear collection, combining fashion with function for an experience that\'s sleek and durable.', '1399.00', 47, '789963452', '2025-06-15 14:14:58', 'product_39_1750000685.jpg'),
(40, 'Superstar 2.0 Training & Gym Shoes For Men  (White , 6)', 5, 'The \'70s brought along a court icon that\'s not going anywhere. Built to outlast every trend, we intentionally replaced high-impact leather with recycled and synthetic leather to minimise impact. A quick fix to your rotation, add this pair made from at least 20% recycled content by weight to your wardrobe.', '1499.00', 20, '963852450', '2025-06-15 14:17:10', 'product_40_1750000700.jpg'),
(41, 'CEAT Speed Master Poplar Willow Cricket Bat For 11 - 13 Yrs  (.4 kg)', 8, 'Dimensions\nWidth: 9.5 cm\nHeight: 75.5 cm\nDepth: 10 cm', '2499.00', 24, '56987485203', '2025-06-15 14:19:32', 'product_41_1750000710.jpg'),
(42, 'Shopeleven Rubber Cricket Tennis Ball Light Weight Tennis Practice Ball Cricket Tennis Ball  (Standard, Pack of 6, Green)', 8, 'This cricket tennis ball is made from natural rubber and durable felt material for consistent feel and reduced shock. Ideal for tennis ball machines, tennis practice. To ensures the all courts tennis balls will play like new for as long as possible, not easy to get broke in normal use. Soft tennis ball for cricket that won\'t lose any pressure over time, providing consistent performance and durability. Perfect for training purpose.', '299.00', 99, '6874501241', '2025-06-15 14:22:24', 'product_42_1750000718.jpg'),
(43, 'Portronics My Buddy D Multipurpose Movable & Adjustable Wood Portable Laptop Table  (Finish Color - Brown, DIY(Do-It-Yourself))', 9, 'Make your work from home easy and convenient with the My Buddy D Multipurpose Portable Laptop table. It is perfect for anywhere in the house or office, as a sofa/bedside table, laptop/computer desk or more. The laptop desk has 4 caster wheels with an integrated safety wheel lock that offers maximum flexibility whatever you gliding, spinning or tilting, easily move to anywhere to set up your workplace immediately. You can adjust the height between 52 cm to 80 cm at your convenience. Setting it up is not an issue, it is delivered with an instruction manual, a set of screws and all necessary tools required that enables you to assemble it just within 10-20 minutes. This ergonomically designed multipurpose stand can provide stable support for weight up to 10 Kilograms.', '1349.00', 47, '9685748256', '2025-06-15 14:23:47', 'product_43_1750000730.jpg'),
(44, 'Shreeji krupa Glass Grocery Container - 1000 ml  (Pack of 2, Clear)', 7, 'The Cube Glass Jar Set includes two 1000ml jars, designed for stylish and practical storage. These glass jars feature a sleek, cube shape that provides a modern touch to your kitchen or pantry. With a generous 1000ml capacity each, they are perfect for storing a variety of items such as grains, pasta, spices, or snacks. The durable, clear glass allows for easy visibility of contents, while the airtight lids keep your food fresh and secure. Ideal for both everyday use and special occasions, these jars combine functionality with a contemporary aesthetic, making them a versatile addition to any home.', '250.00', 10, '69785241201', '2025-06-15 14:25:02', 'product_44_1750000739.jpg'),
(45, 'NIRLON kitchen accessories for cooking pots and pan combo Set of 4 Pieces Non-Stick Coated Cookware Set  (PTFE (Non-stick), Aluminium, 4 - Piece)', 7, 'Nirlon non stick cookware set is an exceptional value while still unique and colorful bringing a sense of style to your kitchen. The non stick exterior is beautiful and functional providing quick and even heat distribution with a stylish non stick finish. The long lasting nonstick cooking surface allows food to release from the pan making cleanup quick and easy.', '1600.00', 15, '30147852012', '2025-06-15 14:26:04', 'product_45_1750000750.jpg'),
(46, 'Men Regular Fit Solid Spread Collar Casual Shirt', 6, 'Introducing the Exquisite Metronaut Popcorn Shirt, a Masterpiece in men\'s fashion. Crafted with the finest materials and meticulous Attention To Detail, This Shirt Embodies Sophistication And Elegance. Step Into The world of Luxury With Metronaut\'s Popcorn Shirt Collection. Each shirt is Designed To Elevate Your Style, Making a Bold Statement Wherever You Go. Impeccably Tailored And Effortlessly Stylish.', '249.00', 30, '35874014856', '2025-06-15 14:27:45', 'product_46_1750000761.jpg'),
(47, 'Men Striped Ankle Length  (Pack of 3)', 6, 'ntroducing our comfortable and stylish Ankle Length Socks for Men! Crafted with precision and designed for performance, these socks are the perfect blend of functionality and fashion, making them an essential addition to any man\'s wardrobe. Features: Premium Quality: Made from a high-quality blend Cotton of materials, these socks ensure durability, breathability, and long-lasting comfort.', '238.00', 250, '35011004551', '2025-06-15 14:29:45', 'product_47_1750000798.jpg'),
(48, 'LG UltraGear 60.96 cm (24 inch) Full HD IPS Panel', 4, 'This LG UltraGear monitor comes with an array of features to enhance its performance, so you can have a smooth gaming experience. It has a rapid refresh rate of 180 Hz, allowing you to elevate your gameplay with fluid visuals. Its 1 ms response time enables you to enjoy lag-free gaming in addition to an IPS panel to deliver exceptional images with enhanced clarity. With the NVIDIA G-Sync and AMD FreeSync compatibility, this monitor enables you with fluid graphics to power up your gaming sessions.', '9999.00', 8, '96871616616', '2025-06-15 14:31:07', 'product_48_1750000771.jpg'),
(49, 'Boult Z40 with Zen ENC Mic', 4, 'Introducing the unstoppable warrior, the Z40 Earbuds. Indulge in crystal-clear sound and immersive bass thanks to the powerful 10 mm BoomX Drivers. With Zen Tech ENC, becomes a thing of the past. Stay connected with the latest Bluetooth 5.3 technology, ensuring a stable and seamless connection for uninterrupted entertainment.', '799.00', 55, '9498426465165', '2025-06-15 14:32:42', 'product_49_1750000866.jpg'),
(50, 'realme Buds T200 Lite', 4, 'The realme Buds T200 Lite TWS is the ultimate companion for those who want to level up their audio game. With a powerful 12.4mm Dynamic Bass Driver, you\'ll experience rich, deep bass that will make your music come alive. And with a whopping 48 hours of total playback, you can jam out all day and all night without worrying about running out of juice.\n\nThanks to dual-mic AI Deep Call Noise Cancellation, you can enjoy crystal-clear calls even in the midst of chaos. Need a quick charge? No problem! Just 10 minutes will give you 5 hours of playback, perfect for those on-the-go moments. These earbuds are also super smooth for gaming and video watching with their Ultra-Low Latency feature. And with IPX4 water resistance, you can take them to the gym, on a run, or even out in the rain without worry.', '1399.00', 30, '255984284656', '2025-06-15 14:33:58', 'product_50_1750000806.jpg'),
(51, 'Samsung Galaxy Z Fold6 5G (Silver Shadow, 512 GB)  (12 GB RAM)', 4, 'Galaxy Z Fold 6\n\nThis Samsung Galaxy Z Fold 6 comes with an array of features to take your everyday game to a whole new level.\n\nGalaxy AI\n\nYou can elevate your style with this Fold 6 smartphone which fits perfectly in your pocket. This smartphone can be folded to attain a compact size and also functions as a large screen to cater to your changing needs.\n\nPhoto Assist\n\nEffortless editing, courtesy of Galaxy AI. With Photo Assist, just hold your finger down on an object to move, erase or enlarge it; adjust angles or fill backgrounds just as easily.', '161999.00', 5, '6246498465', '2025-06-15 14:36:04', 'product_51_1750000814.jpg'),
(52, 'Samsung Galaxy S24 Ultra 5G', 4, 'Behold the Samsung Galaxy S24 Ultra smartphone, an exceptional amalgamation of incredible technology and superior sophistication. Whether you\'re typing up a storm or jotting something down, Note Assist makes a long story short. New AI-powered editing options let you get the photo you want, like relocating objects and intelligently filling in the space they left behind. With a durable shield of titanium built right into the frame and better scratch resistance with Corning Gorilla Armor, your IP68 water and dust-resistant Galaxy S24 Ultra is ready for adventure. Write, tap, and navigate with the precision your fingers wish they had on the new, flat display.', '85485.00', 4, '99649246464', '2025-06-15 14:37:10', 'product_52_1750000822.jpg'),
(53, 'KSF Black PVC Hexa Dumbbell Set,', 8, 'KSF Presents you Hexa PVC 5KG Black Dumbbell. Hex 5KG Dumbbell are made of Plastic and filled with sand. The outer Shell is PVC Plastic and Inner of shell is filled with sand. When it Comes to home gym fitness these Fixed Dumbbell are one of the best choice. You can perform various exercises like Biceps, Triceps and other fitness exercise. These Dumbbell are highly durable and built quality is very good. Order your Dumbbells now and build your muscles.', '549.00', 20, '989424654651', '2025-06-15 14:38:29', 'product_53_1750000832.jpg');

-- --------------------------------------------------------

--
-- Table structure for table `sales`
--

CREATE TABLE `sales` (
  `sale_id` int(11) NOT NULL,
  `customer_name` varchar(100) DEFAULT NULL,
  `customer_mobile_number` char(10) DEFAULT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  `payment_status` enum('paid','pending') NOT NULL,
  `payment_method` enum('cash','UPI','credit','debit') NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `sold_by` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `sales`
--

INSERT INTO `sales` (`sale_id`, `customer_name`, `customer_mobile_number`, `total_amount`, `payment_status`, `payment_method`, `created_at`, `sold_by`) VALUES
(25, 'Saket', '9653125087', '109900.00', 'paid', 'cash', '2025-04-15 15:23:30', 1),
(26, 'Samir', '7085290817', '1698.00', 'paid', 'cash', '2025-04-15 15:24:22', 1),
(27, 'Sunil', '9801344508', '12897.00', 'paid', 'cash', '2025-03-15 15:26:00', 1),
(28, 'Saket', '9658407521', '1399.00', 'paid', 'cash', '2025-04-15 15:26:35', 1),
(29, 'Himaksh', '9613245801', '2798.00', 'paid', 'cash', '2025-05-15 15:27:01', 1),
(30, 'Prateek', '9312345280', '1349.00', 'paid', 'cash', '2025-05-15 15:27:28', 1),
(31, 'Rashgulli', '9823164528', '1349.00', 'paid', 'cash', '2025-06-12 15:27:56', 1),
(32, 'Rahul', '9632580569', '109900.00', 'paid', 'cash', '2025-06-14 15:28:33', 1),
(33, 'Rohit', '9653127813', '449.00', 'paid', 'cash', '2025-06-14 15:29:02', 1),
(34, 'Prakash', '9653152369', '1399.00', 'paid', 'cash', '2025-06-15 15:29:38', 1),
(35, 'Praveen', '9685134085', '109900.00', 'paid', 'cash', '2025-06-15 15:33:18', 1),
(36, 'Prakash', '9856785203', '112648.00', 'paid', 'cash', '2025-06-15 15:39:33', 1);

-- --------------------------------------------------------

--
-- Table structure for table `sales_items`
--

CREATE TABLE `sales_items` (
  `sale_item_id` int(11) NOT NULL,
  `sale_id` int(11) DEFAULT NULL,
  `product_id` int(11) DEFAULT NULL,
  `quantity` int(11) NOT NULL,
  `price` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `sales_items`
--

INSERT INTO `sales_items` (`sale_item_id`, `sale_id`, `product_id`, `quantity`, `price`) VALUES
(42, 25, 34, 1, '109900.00'),
(43, 26, 35, 1, '1349.00'),
(44, 26, 38, 1, '349.00'),
(45, 27, 37, 1, '1499.00'),
(46, 27, 50, 1, '1399.00'),
(47, 27, 48, 1, '9999.00'),
(48, 28, 39, 1, '1399.00'),
(49, 29, 41, 1, '2499.00'),
(50, 29, 42, 1, '299.00'),
(51, 30, 35, 1, '1349.00'),
(52, 31, 43, 1, '1349.00'),
(53, 32, 34, 1, '109900.00'),
(54, 33, 36, 1, '449.00'),
(55, 34, 39, 1, '1399.00'),
(56, 35, 34, 1, '109900.00'),
(57, 36, 39, 1, '1399.00'),
(58, 36, 34, 1, '109900.00'),
(59, 36, 35, 1, '1349.00');

-- --------------------------------------------------------

--
-- Table structure for table `stock_additions`
--

CREATE TABLE `stock_additions` (
  `stock_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `added_quantity` int(11) NOT NULL,
  `added_by` int(11) DEFAULT NULL,
  `remarks` text DEFAULT NULL,
  `added_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `stock_additions`
--

INSERT INTO `stock_additions` (`stock_id`, `product_id`, `added_quantity`, `added_by`, `remarks`, `added_at`) VALUES
(8, 38, 5, 1, '', '2025-06-15 15:32:00'),
(9, 34, 2, 1, '', '2025-06-15 15:32:00');

--
-- Triggers `stock_additions`
--
DELIMITER $$
CREATE TRIGGER `after_stock_added` AFTER INSERT ON `stock_additions` FOR EACH ROW BEGIN
  UPDATE products
  SET quantity = quantity + NEW.added_quantity
  WHERE product_id = NEW.product_id;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `username` varchar(100) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `user_type` enum('admin','staff') NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `profile_pic` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `username`, `email`, `password`, `user_type`, `created_at`, `profile_pic`) VALUES
(1, 'Prakash Sirvi', 'prakash@gmail.com', '12345678', 'admin', '2025-04-28 14:55:37', 'user_1_1749743070.jpg'),
(28, 'Aarav Sharma', 'aaravsharma123@gmail.com', '12345678', 'staff', '2025-06-15 14:49:59', 'user_28_1750000276.jpg'),
(29, 'Rohan Mehta', 'rohan123@gmail.com', '12345678', 'staff', '2025-06-15 14:50:22', 'user_29_1750000319.jpg'),
(30, 'Karan Deshmukh', 'karan123@gmail.com', '12345678', 'staff', '2025-06-15 14:50:47', 'user_30_1750000347.jpg'),
(31, 'Vivek Reddy', 'vivekreddy@gmail.com', '12345678', 'staff', '2025-06-15 14:51:02', 'user_31_1750000379.jpg'),
(32, 'Anush Kulal', 'anush123@gmail.com', '12345678', 'staff', '2025-06-15 14:51:36', NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `cart`
--
ALTER TABLE `cart`
  ADD PRIMARY KEY (`cart_id`),
  ADD KEY `added_by` (`added_by`),
  ADD KEY `product_id` (`product_id`);

--
-- Indexes for table `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`category_id`);

--
-- Indexes for table `payments`
--
ALTER TABLE `payments`
  ADD PRIMARY KEY (`payment_id`),
  ADD KEY `sale_id` (`sale_id`);

--
-- Indexes for table `products`
--
ALTER TABLE `products`
  ADD PRIMARY KEY (`product_id`),
  ADD KEY `category_id` (`category_id`);

--
-- Indexes for table `sales`
--
ALTER TABLE `sales`
  ADD PRIMARY KEY (`sale_id`),
  ADD KEY `fk_sold_by` (`sold_by`);

--
-- Indexes for table `sales_items`
--
ALTER TABLE `sales_items`
  ADD PRIMARY KEY (`sale_item_id`),
  ADD KEY `sales_items_ibfk_1` (`sale_id`),
  ADD KEY `sales_items_ibfk_2` (`product_id`);

--
-- Indexes for table `stock_additions`
--
ALTER TABLE `stock_additions`
  ADD PRIMARY KEY (`stock_id`),
  ADD KEY `product_id` (`product_id`),
  ADD KEY `added_by` (`added_by`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `cart`
--
ALTER TABLE `cart`
  MODIFY `cart_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=67;

--
-- AUTO_INCREMENT for table `categories`
--
ALTER TABLE `categories`
  MODIFY `category_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `payments`
--
ALTER TABLE `payments`
  MODIFY `payment_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `products`
--
ALTER TABLE `products`
  MODIFY `product_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=54;

--
-- AUTO_INCREMENT for table `sales`
--
ALTER TABLE `sales`
  MODIFY `sale_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=37;

--
-- AUTO_INCREMENT for table `sales_items`
--
ALTER TABLE `sales_items`
  MODIFY `sale_item_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=60;

--
-- AUTO_INCREMENT for table `stock_additions`
--
ALTER TABLE `stock_additions`
  MODIFY `stock_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=33;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `cart`
--
ALTER TABLE `cart`
  ADD CONSTRAINT `cart_ibfk_1` FOREIGN KEY (`added_by`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `cart_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `payments`
--
ALTER TABLE `payments`
  ADD CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`sale_id`) REFERENCES `sales` (`sale_id`);

--
-- Constraints for table `products`
--
ALTER TABLE `products`
  ADD CONSTRAINT `products_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`);

--
-- Constraints for table `sales`
--
ALTER TABLE `sales`
  ADD CONSTRAINT `fk_sold_by` FOREIGN KEY (`sold_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL;

--
-- Constraints for table `sales_items`
--
ALTER TABLE `sales_items`
  ADD CONSTRAINT `sales_items_ibfk_1` FOREIGN KEY (`sale_id`) REFERENCES `sales` (`sale_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `sales_items_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `stock_additions`
--
ALTER TABLE `stock_additions`
  ADD CONSTRAINT `stock_additions_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `stock_additions_ibfk_2` FOREIGN KEY (`added_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
