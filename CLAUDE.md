# AgriCloud - Smart Farm Management System

## Progress Tracker — Module 1: User Management (Farouk)

### Done
- [x] Database setup (users, roles tables)
- [x] Login screen with authentication (bcrypt)
- [x] Register screen with role selection (Farmer/Customer)
- [x] Input validation (email regex, phone regex, name min 2 chars) on Register, Profile, Admin form
- [x] Dashboard with sidebar navigation (admin vs regular user)
- [x] Admin: Users CRUD (Add, Edit, Delete)
- [x] Admin: Roles CRUD (Add, Edit, Delete)
- [x] Admin: Search/filter on users table (live search + status filter)
- [x] Admin: Block/Unblock user toggle with self-block prevention
- [x] Admin: Statistics page (total/active/inactive/blocked cards, pie chart by role, bar chart registrations last 7 days)
- [x] User: Profile editing (name, email, phone, password)
- [x] Forgot Password flow (email-based 6-digit code via Gmail SMTP)
- [x] Blocked user login prevention (shows error on login screen)
- [x] ValidationUtils utility class
- [x] EmailUtils utility class (Gmail SMTP SSL on port 465)
- [x] CSS styling for all views (login, dashboard, tables, stat cards, search/filter)

### To Do
- [ ] Profile picture upload
- [ ] Email verification on registration
- [ ] PDF/CSV export of user list
- [ ] Remember me (auto-login)

---

## Progress Tracker — Module 2: Farm Management (Shahed)

### Done
- [x] Farm.java model (id, userId, name, location, latitude, longitude, area, farmType, description, image, status, approvedAt, approvedBy, timestamps)
- [x] Field.java model (id, farmId, name, area, soilType, cropType, coordinates, status, timestamps)
- [x] FarmService.java with CRUD + getByUserId, approve/reject methods
- [x] FieldService.java with CRUD + getByFarmId
- [x] Admin: Farms view (all farms table, search/filter, Add/Edit/Delete, Approve/Reject)
- [x] Farmer: My Farms view (own farms only, Add/Edit/Delete)
- [x] Fields nested view (click farm → manage its fields)
- [x] Interactive map picker (WebView + Leaflet/OpenStreetMap) for lat/lng coordinates
- [x] CSS: .action-button-approve (green) and .action-button-reject (red) styles
- [x] Farm status workflow: pending → approved/rejected

### To Do
- [ ] Farm image upload
- [ ] Map view of all farms

---

## Progress Tracker — Module 3: Market Management (Ghada)

### Done
- [x] Product.java model (id, userId, farmId, name, description, price, quantity, unit, category, image, status, views, approvedAt, approvedBy, timestamps)
- [x] Order.java model (id, customerId, productId, sellerId, quantity, unitPrice, totalPrice, status, shipping info, dates, timestamps)
- [x] CartItem.java model (id, userId, productId, quantity, transient product fields for display)
- [x] ProductService.java with CRUD + getByUserId, getApproved, incrementViews, decrementQuantity
- [x] OrderService.java with CRUD + getByCustomerId, getBySellerId, updateStatus
- [x] CartService.java with addToCart, updateQuantity, removeFromCart, clearCart, getCartItems, getCartTotal
- [x] Admin: Products view (all products, search/filter, Approve/Reject)
- [x] Admin: Orders view (all orders, update status)
- [x] Farmer: My Products view (own products, Add/Edit/Delete)
- [x] Farmer: My Orders view (orders for their products, update status)
- [x] Customer: Shop view (browse approved products, add to cart)
- [x] Customer: Cart view (view cart, update quantities, remove items, checkout)
- [x] Customer: My Orders view (own orders with status)
- [x] Checkout flow with shipping address form
- [x] Category dropdown (Fruits, Vegetables, Dairy, Meat, Grains, Herbs, Honey, Eggs, Other)

### To Do
- [ ] Product image upload
- [ ] Order notifications
- [ ] Payment integration

---

## Progress Tracker — Module 4: Blog Management (Rania)

### Done
- [x] Post.java model (id, userId, title, slug, content, excerpt, image, category, tags, status, views, publishedAt, timestamps, transient userName)
- [x] Comment.java model (id, postId, userId, parentCommentId, content, status, approvedAt, approvedBy, timestamps, transient userName)
- [x] PostService.java with CRUD + getByUserId, getPublished, incrementViews, publish/unpublish, author name joins
- [x] CommentService.java with CRUD + getByPostId, getApprovedByPostId, approve/reject
- [x] Admin: Posts management view (all posts, Add/Edit/Delete, Publish/Unpublish)
- [x] Admin: Blog browse view (read posts, delete any comment, block/unblock users from comments)
- [x] Farmer/Customer: Blog view (browse published posts, double-click to open)
- [x] Farmer/Customer: My Posts view (own posts, Add/Edit/Delete, Publish)
- [x] Post detail view with comments section
- [x] Add comment functionality (auto-approved, no moderation needed)
- [x] Post owner can delete any comment on their post
- [x] Comment author can delete their own comment
- [x] Admin block/unblock toggle button on comments (shows "Block User" or "Blocked")
- [x] Author name displayed in blog table and post detail
- [x] Category dropdown for posts (Agriculture, Technology, Farming Tips, Livestock, Organic, Weather, Equipment, Success Stories, Other)
- [x] Enter key login support (press Enter in email/password field to login)
- [x] Fix: dashboard.fxml XML declaration must be on line 1

### To Do
- [ ] Post image upload
- [ ] Rich text editor for post content
- [ ] Comment replies (nested comments)
- [ ] Like/reaction system

---

## Progress Tracker — Module 5: Event Management (Ayman)

### To Do
- [ ] Event.java model
- [ ] Participation.java model
- [ ] EventService.java with CRUD
- [ ] ParticipationService.java with CRUD
- [ ] Admin: Events management view
- [ ] User: Browse events, register participation
- [ ] Event calendar view

---

## Code Patterns & Conventions

### Models
- Package: `esprit.farouk.models`
- Use `long` for IDs (primitive), `Long` for nullable foreign keys
- Use `LocalDateTime` for timestamps
- Constructors: empty + minimal + full
- Inline getters/setters on single lines
- Transient fields (e.g., `userName`) for display data from JOINs

### Services
- Package: `esprit.farouk.services`
- `Connection` field from `DatabaseConnection.getConnection()`
- `PreparedStatement` for all queries (SQL injection prevention)
- `mapRow(ResultSet)` helper method for object mapping
- Null-safe timestamp handling: `if (timestamp != null) obj.setField(timestamp.toLocalDateTime())`
- Nullable primitives: use `rs.wasNull()` after `rs.getLong()`

### Controllers
- Package: `esprit.farouk.controllers`
- DashboardController: All views built programmatically in Java (no separate FXML per view)
- Content swapped via `contentArea.getChildren().clear()` + add new layout
- Tables: `FilteredList` + `SortedList` pattern for live search/filter
- Forms: `Dialog<ButtonType>` with `GridPane`, validation loop (`while(true)` re-show on error)
- Role-based sidebar: three branches (admin, farmer, customer)

### FXML
- XML declaration `<?xml version="1.0"...?>` MUST be on line 1 (no empty lines before)
- `onAction="#methodName"` for button clicks and Enter key on text fields

---

## Project Overview


- **Version:** 1.0
- **Team Size:** 5 Members
- **Total Modules:** 5
- **Total User Stories:** 98
- **Database Tables:** 14

---

## Team & Module Assignment

| Module | Responsible | Entities | Priority |
|--------|-------------|----------|----------|
| User Management | Farouk | User, Role | 100-85 |
| Farm Management | Shahed | Farm, Field | 100-85 |
| Market Management | Ghada | Product, Order | 100-85 |
| Blog Management | Rania | Post, Comment | 100-85 |
| Event Management | Ayman | Event, Participation | 100-94 |

---

## Technology Stack

### Backend & Desktop Application
- **Framework:** JavaFX 17+
- **Language:** Java 17+
- **Build Tool:** Maven / Gradle
- **Database:** MySQL 8.0+ / MariaDB 10.5+ (via WAMP)
- **HTTP Client:** Apache HttpClient / OkHttp (for future API calls)
- **JSON Parser:** Gson / Jackson

### Database
- **DBMS:** MySQL 8.0+ / MariaDB 10.5+ (WAMP)
- **GUI Tool:** phpMyAdmin (included with WAMP)
- **Character Set:** UTF8MB4
- **Engine:** InnoDB
- **Total Tables:** 14

---

## Database Schema

### Module 1: User Management (Farouk)

#### Table: users
```sql
CREATE TABLE users (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT UNSIGNED NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    profile_picture VARCHAR(255),
    status ENUM('active', 'inactive', 'blocked') DEFAULT 'active',
    email_verified_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE RESTRICT,
    INDEX idx_email (email),
    INDEX idx_role_id (role_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### Table: roles
```sql
CREATE TABLE roles (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    permissions JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

**Relationships:**
- User belongsTo Role (Many-to-One)

---

### Module 2: Farm Management (Shahed)

#### Table: farms
```sql
CREATE TABLE farms (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    name VARCHAR(150) NOT NULL,
    location VARCHAR(255) NOT NULL,
    latitude DECIMAL(10,8),
    longitude DECIMAL(11,8),
    area DECIMAL(10,2),
    farm_type VARCHAR(50),
    description TEXT,
    image VARCHAR(255),
    status ENUM('pending', 'approved', 'rejected', 'inactive') DEFAULT 'pending',
    approved_at TIMESTAMP NULL,
    approved_by BIGINT UNSIGNED,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (approved_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    CHECK (area >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### Table: fields
```sql
CREATE TABLE fields (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    farm_id BIGINT UNSIGNED NOT NULL,
    name VARCHAR(100) NOT NULL,
    area DECIMAL(10,2) NOT NULL,
    soil_type VARCHAR(50),
    crop_type VARCHAR(50),
    coordinates JSON,
    status ENUM('active', 'inactive', 'fallow') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (farm_id) REFERENCES farms(id) ON DELETE CASCADE,
    INDEX idx_farm_id (farm_id),
    CHECK (area > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

**Relationships:**
- Farm belongsTo User (Many-to-One)
- Farm hasMany Field (One-to-Many)
- Field belongsTo Farm (Many-to-One)

---

### Module 3: Market Management (Ghada)

#### Table: products
```sql
CREATE TABLE products (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    farm_id BIGINT UNSIGNED,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    unit VARCHAR(20) NOT NULL,
    category VARCHAR(50),
    image VARCHAR(255),
    status ENUM('pending', 'approved', 'rejected', 'sold_out') DEFAULT 'pending',
    views INT DEFAULT 0,
    approved_at TIMESTAMP NULL,
    approved_by BIGINT UNSIGNED,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (farm_id) REFERENCES farms(id) ON DELETE SET NULL,
    FOREIGN KEY (approved_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_farm_id (farm_id),
    INDEX idx_status (status),
    FULLTEXT idx_search (name, description),
    CHECK (price >= 0),
    CHECK (quantity >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### Table: orders
```sql
CREATE TABLE orders (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT UNSIGNED NOT NULL,
    product_id BIGINT UNSIGNED NOT NULL,
    seller_id BIGINT UNSIGNED NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    status ENUM('pending', 'confirmed', 'processing', 'shipped', 'delivered', 'cancelled') DEFAULT 'pending',
    shipping_address TEXT NOT NULL,
    shipping_city VARCHAR(100),
    shipping_postal VARCHAR(20),
    notes TEXT,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    delivery_date DATE,
    cancelled_at TIMESTAMP NULL,
    cancelled_reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT,
    FOREIGN KEY (seller_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_customer_id (customer_id),
    INDEX idx_product_id (product_id),
    INDEX idx_seller_id (seller_id),
    INDEX idx_status (status),
    CHECK (quantity > 0),
    CHECK (total_price >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### Table: shopping_cart
```sql
CREATE TABLE shopping_cart (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    product_id BIGINT UNSIGNED NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    UNIQUE KEY idx_user_product (user_id, product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

**Relationships:**
- Product belongsTo User (Many-to-One)
- Product belongsTo Farm (Many-to-One)
- Order belongsTo User (Customer) (Many-to-One)
- Order belongsTo Product (Many-to-One)
- Order belongsTo User (Seller) (Many-to-One)

---

### Module 4: Blog Management (Rania)

#### Table: posts
```sql
CREATE TABLE posts (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    content LONGTEXT NOT NULL,
    excerpt TEXT,
    image VARCHAR(255),
    category VARCHAR(50),
    tags JSON,
    status ENUM('draft', 'published', 'unpublished') DEFAULT 'draft',
    views INT DEFAULT 0,
    published_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_slug (slug),
    INDEX idx_status (status),
    FULLTEXT idx_search (title, content)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### Table: comments
```sql
CREATE TABLE comments (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT UNSIGNED NOT NULL,
    user_id BIGINT UNSIGNED NOT NULL,
    parent_comment_id BIGINT UNSIGNED,
    content TEXT NOT NULL,
    status ENUM('pending', 'approved', 'rejected', 'deleted') DEFAULT 'pending',
    approved_at TIMESTAMP NULL,
    approved_by BIGINT UNSIGNED,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_comment_id) REFERENCES comments(id) ON DELETE CASCADE,
    FOREIGN KEY (approved_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_post_id (post_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

**Relationships:**
- Post belongsTo User (Many-to-One)
- Post hasMany Comment (One-to-Many)
- Comment belongsTo Post (Many-to-One)
- Comment belongsTo User (Many-to-One)
- Comment belongsTo Comment (Self-referencing)

---

### Module 5: Event Management (Ayman)

#### Table: events
```sql
CREATE TABLE events (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    title VARCHAR(200) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    event_date DATETIME NOT NULL,
    end_date DATETIME,
    location VARCHAR(255) NOT NULL,
    latitude DECIMAL(10,8),
    longitude DECIMAL(11,8),
    capacity INT,
    image VARCHAR(255),
    category VARCHAR(50),
    status ENUM('upcoming', 'ongoing', 'completed', 'cancelled') DEFAULT 'upcoming',
    registration_deadline DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_event_date (event_date),
    CHECK (capacity > 0 OR capacity IS NULL)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### Table: participations
```sql
CREATE TABLE participations (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT UNSIGNED NOT NULL,
    user_id BIGINT UNSIGNED NOT NULL,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('pending', 'confirmed', 'cancelled', 'attended') DEFAULT 'pending',
    notes TEXT,
    cancelled_at TIMESTAMP NULL,
    cancelled_reason TEXT,
    attended BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY idx_event_user (event_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

**Relationships:**
- Event belongsTo User (Many-to-One)
- Event hasMany Participation (Many-to-Many through Participation)
- User hasMany Participation (Many-to-Many through Participation)

---

## Java Models (For JavaFX Desktop App)

### Module 1: User Management

```java
// User.java
public class User {
    private Long id;
    private Long roleId;
    private String name;
    private String email;
    private String phone;
    private String profilePicture;
    private String status; // active, inactive, blocked
    private LocalDateTime emailVerifiedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors, Getters, Setters, toString()
}

// Role.java
public class Role {
    private Long id;
    private String name;
    private String description;
    private String permissions; // JSON string
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors, Getters, Setters
}
```

### Module 2: Farm Management

```java
// Farm.java
public class Farm {
    private Long id;
    private Long userId;
    private String name;
    private String location;
    private Double latitude;
    private Double longitude;
    private Double area;
    private String farmType;
    private String description;
    private String image;
    private String status; // pending, approved, rejected, inactive
    private LocalDateTime approvedAt;
    private Long approvedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors, Getters, Setters
}

// Field.java
public class Field {
    private Long id;
    private Long farmId;
    private String name;
    private Double area;
    private String soilType;
    private String cropType;
    private String coordinates; // JSON string
    private String status; // active, inactive, fallow
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors, Getters, Setters
}
```

### Module 3: Market Management

```java
// Product.java
public class Product {
    private Long id;
    private Long userId;
    private Long farmId;
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
    private String unit;
    private String category;
    private String image;
    private String status; // pending, approved, rejected, sold_out
    private Integer views;
    private LocalDateTime approvedAt;
    private Long approvedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors, Getters, Setters
}

// Order.java
public class Order {
    private Long id;
    private Long customerId;
    private Long productId;
    private Long sellerId;
    private Integer quantity;
    private Double unitPrice;
    private Double totalPrice;
    private String status; // pending, confirmed, processing, shipped, delivered, cancelled
    private String shippingAddress;
    private String shippingCity;
    private String shippingPostal;
    private String notes;
    private LocalDateTime orderDate;
    private LocalDate deliveryDate;
    private LocalDateTime cancelledAt;
    private String cancelledReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors, Getters, Setters
}
```

### Module 4: Blog Management

```java
// Post.java
public class Post {
    private Long id;
    private Long userId;
    private String title;
    private String slug;
    private String content;
    private String excerpt;
    private String image;
    private String category;
    private String tags; // JSON string
    private String status; // draft, published, unpublished
    private Integer views;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors, Getters, Setters
}

// Comment.java
public class Comment {
    private Long id;
    private Long postId;
    private Long userId;
    private Long parentCommentId;
    private String content;
    private String status; // pending, approved, rejected, deleted
    private LocalDateTime approvedAt;
    private Long approvedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors, Getters, Setters
}
```

### Module 5: Event Management

```java
// Event.java
public class Event {
    private Long id;
    private Long userId;
    private String title;
    private String slug;
    private String description;
    private LocalDateTime eventDate;
    private LocalDateTime endDate;
    private String location;
    private Double latitude;
    private Double longitude;
    private Integer capacity;
    private String image;
    private String category;
    private String status; // upcoming, ongoing, completed, cancelled
    private LocalDateTime registrationDeadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors, Getters, Setters
}

// Participation.java
public class Participation {
    private Long id;
    private Long eventId;
    private Long userId;
    private LocalDateTime registrationDate;
    private String status; // pending, confirmed, cancelled, attended
    private String notes;
    private LocalDateTime cancelledAt;
    private String cancelledReason;
    private Boolean attended;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors, Getters, Setters
}
```

---

## Database Connection in Java

### JDBC Configuration

#### DatabaseConfig.java
```java
package com.agricloud.config;

public class DatabaseConfig {
    // WAMP MySQL Configuration
    public static final String DB_HOST = "localhost";
    public static final String DB_PORT = "3306";
    public static final String DB_NAME = "agricloud";
    public static final String DB_USER = "root";
    public static final String DB_PASS = ""; // Empty for default WAMP
    
    // JDBC URL
    public static final String DB_URL = 
        "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME + 
        "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    
    // JDBC Driver
    public static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
}
```

#### DatabaseConnection.java
```java
package com.agricloud.database;

import com.agricloud.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Connection connection = null;
    
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName(DatabaseConfig.DB_DRIVER);
                connection = DriverManager.getConnection(
                    DatabaseConfig.DB_URL,
                    DatabaseConfig.DB_USER,
                    DatabaseConfig.DB_PASS
                );
                System.out.println("Database connected successfully!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database connection failed!");
            e.printStackTrace();
        }
        return connection;
    }
    
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

---

## Project Structure

### JavaFX Desktop App Structure
```
src/
├── main/
│   ├── java/
│   │   └── com/agricloud/
│   │       ├── Main.java
│   │       ├── config/
│   │       │   └── ApiConfig.java
│   │       ├── models/
│   │       │   ├── User.java
│   │       │   ├── Farm.java
│   │       │   ├── Product.java
│   │       │   ├── Post.java
│   │       │   └── Event.java
│   │       ├── services/
│   │       │   ├── ApiService.java
│   │       │   ├── AuthService.java
│   │       │   ├── UserService.java
│   │       │   ├── FarmService.java
│   │       │   ├── ProductService.java
│   │       │   ├── PostService.java
│   │       │   └── EventService.java
│   │       ├── controllers/
│   │       │   ├── LoginController.java
│   │       │   ├── DashboardController.java
│   │       │   ├── UserController.java
│   │       │   ├── FarmController.java
│   │       │   ├── ProductController.java
│   │       │   ├── PostController.java
│   │       │   └── EventController.java
│   │       └── utils/
│   │           ├── HttpClient.java
│   │           └── JsonParser.java
│   └── resources/
│       ├── fxml/
│       │   ├── login.fxml
│       │   ├── dashboard.fxml
│       │   ├── users.fxml
│       │   ├── farms.fxml
│       │   ├── products.fxml
│       │   ├── posts.fxml
│       │   └── events.fxml
│       ├── css/
│       │   └── style.css
│       └── images/
└── test/
```

```
src/
├── Controller/
├── Entity/
├── Repository/
└── Service/
```

---

## Development Workflow

### 1. Database Setup with WAMP + phpMyAdmin

#### Option A: Using phpMyAdmin (Recommended for WAMP)
1. **Start WAMP Server**
    - Click on WAMP icon in system tray
    - Make sure all services are green (Apache & MySQL running)

2. **Open phpMyAdmin**
    - Open browser and go to: `http://localhost/phpmyadmin`
    - Login (default: username=`root`, password=`empty` or `root`)

3. **Create Database**
    - Click on "New" in left sidebar
    - Database name: `agricloud`
    - Collation: `utf8mb4_unicode_ci`
    - Click "Create"

4. **Import SQL Schema**
    - Select `agricloud` database from left sidebar
    - Click on "SQL" tab at the top
    - Open the file `SQL_COMMANDS_GUIDE.txt`
    - Copy all SQL commands
    - Paste into the SQL tab in phpMyAdmin
    - Click "Go" button
    - Wait for success message

5. **Verify Tables**
    - Click on `agricloud` database in left sidebar
    - You should see 14 tables:
        * roles
        * users
        * user_activity_logs
        * farms
        * fields
        * products
        * orders
        * shopping_cart
        * posts
        * comments
        * events
        * participations
        * (and any additional tables)

#### Option B: Using MySQL Command Line (Alternative)
```bash
# Open MySQL command line from WAMP menu
# Or navigate to: C:\wamp64\bin\mysql\mysql8.x.x\bin\mysql.exe

# Login
mysql -u root -p

# Create database
CREATE DATABASE agricloud CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# Use database
USE agricloud;

# Copy and paste SQL commands from SQL_COMMANDS_GUIDE.txt here

# Verify tables
SHOW TABLES;
```

#### WAMP Database Configuration
```
Host: localhost (or 127.0.0.1)
Port: 3306 (default)
Username: root
Password: (empty by default, or 'root')
Database: agricloud
```


#### WAMP Configuration
   ```
   C:\wamp64\www\agricloud-web\
   ```

2. **Install Composer** (if not already installed)
    - Install globally on Windows

   ```bash
   # Open Command Prompt or PowerShell in project directory
   cd C:\wamp64\www\agricloud-web
   
   # Install dependencies
   ```

4. **Configure Database Connection**
    - Update DATABASE_URL:
   ```env
   # For WAMP default configuration
   DATABASE_URL="mysql://root:@127.0.0.1:3306/agricloud?serverVersion=8.0"
   
   # If you set a password for root:
   DATABASE_URL="mysql://root:your_password@127.0.0.1:3306/agricloud?serverVersion=8.0"
   ```

5. **Create/Update Entities from Database**
   ```bash
   # Generate entities from existing database
   
   # Generate getters/setters
   
   # Clear cache
   ```


   ```bash
   symfony server:start
   
   # Access at: http://127.0.0.1:8000
   ```

**Option B: Using PHP Built-in Server**
   ```bash
   php -S localhost:8000 -t public
   
   # Access at: http://localhost:8000
   ```

**Option C: Using WAMP Apache** (Configure Virtual Host)
- Edit: `C:\wamp64\bin\apache\apache2.x.x\conf\extra\httpd-vhosts.conf`
- Add:
   ```apache
   <VirtualHost *:80>
       DocumentRoot "C:/wamp64/www/agricloud-web/public"
       ServerName agricloud.local
       <Directory "C:/wamp64/www/agricloud-web/public">
           AllowOverride All
           Require all granted
       </Directory>
   </VirtualHost>
   ```
- Edit: `C:\Windows\System32\drivers\etc\hosts` (Run as Administrator)
- Add: `127.0.0.1 agricloud.local`
- Restart WAMP
- Access at: `http://agricloud.local`

7. **Test API**
   ```
   # Test endpoint
   http://localhost:8000/api
   
   # Or if using virtual host
   http://agricloud.local/api
   ```

### 3. JavaFX Setup (IntelliJ)
```bash
# Maven dependencies (pom.xml)
<dependencies>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>17.0.2</version>
    </dependency>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-fxml</artifactId>
        <version>17.0.2</version>
    </dependency>
    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.10</version>
    </dependency>
    <dependency>
        <groupId>com.squareup.okhttp3</groupId>
        <artifactId>okhttp</artifactId>
        <version>4.10.0</version>
    </dependency>
</dependencies>
```

---

## Testing Credentials

### Default Admin Account
```
Email: admin@agricloud.com
Password: admin123
```

**⚠️ IMPORTANT: Change this password immediately in production!**

---

## Git Workflow

### Branch Strategy
```
main (production)
├── develop (development)
│   ├── feature/module1-user-management (Farouk)
│   ├── feature/module2-farm-management (Shahed)
│   ├── feature/module3-market-management (Ghada)
│   ├── feature/module4-blog-management (Rania)
│   └── feature/module5-event-management (Ayman)
```

### Commit Convention
```
feat: Add user login functionality
fix: Fix farm creation bug
docs: Update API documentation
style: Format code
refactor: Refactor user service
test: Add unit tests for products
chore: Update dependencies
```

---

## Priority Implementation Order

### Phase 1: Foundation (Weeks 1-2)
- [ ] Database setup
- [ ] User authentication (Module 1)
- [ ] Basic API structure
- [ ] Desktop app login screen

### Phase 2: Core Modules (Weeks 3-5)
- [ ] Farm Management (Module 2)
- [ ] Market Management (Module 3)
- [ ] Blog Management (Module 4)
- [ ] Event Management (Module 5)

### Phase 3: BackOffice (Weeks 6-7)
- [ ] Admin dashboards
- [ ] Approval workflows
- [ ] Statistics and reporting

### Phase 4: Integration & Testing (Weeks 8-9)
- [ ] API integration
- [ ] Desktop-Web sync
- [ ] User testing
- [ ] Bug fixes

### Phase 5: Deployment (Week 10)
- [ ] Production setup
- [ ] Security audit
- [ ] Documentation
- [ ] Launch

---

## WAMP-Specific Configuration

### WAMP Directory Structure
```
C:\wamp64\
├── www\
│   └── index.php               # WAMP homepage
├── bin\
│   ├── apache\
│   ├── mysql\
│   └── php\
└── logs\
```

### Common WAMP Issues & Solutions

#### Issue 1: Port 80 Already in Use
**Solution:**
- Stop Skype or other applications using port 80
- Change Apache port in `httpd.conf`
- Or use port 8080: `Listen 8080`

#### Issue 2: MySQL Not Starting
**Solution:**
- Check if port 3306 is already in use
- Restart WAMP services
- Check MySQL error logs in `C:\wamp64\logs\mysql_error.log`

#### Issue 3: phpMyAdmin 403 Forbidden
**Solution:**
- Edit `C:\wamp64\alias\phpmyadmin.conf`
- Change `Require local` to `Require all granted`
- Restart Apache

#### Issue 4: PHP Extensions Not Loading
**Solution:**
- Left-click WAMP icon → PHP → PHP Extensions
- Enable: mysqli, pdo_mysql, mbstring, openssl, fileinfo, intl

### Accessing Your Applications

| Application | URL | Purpose |
|------------|-----|---------|
| phpMyAdmin | http://localhost/phpmyadmin | Database management |
| WAMP Dashboard | http://localhost | WAMP homepage |

### Database Connection Settings

```env
DATABASE_URL="mysql://root:@127.0.0.1:3306/agricloud?serverVersion=8.0"
```

#### For JavaFX (ApiConfig.java)
```java
public class DatabaseConfig {
    public static final String DB_HOST = "localhost";
    public static final String DB_PORT = "3306";
    public static final String DB_NAME = "agricloud";
    public static final String DB_USER = "root";
    public static final String DB_PASS = ""; // Empty for default WAMP
    
    public static final String DB_URL = 
        "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;
}
```


1. **Verify PHP Version**
   ```bash
   php -v
   # Should be PHP 8.0 or higher
   ```

2. **Enable Required Extensions** (via WAMP icon)
    - mysqli
    - pdo_mysql
    - mbstring
    - openssl
    - curl
    - fileinfo
    - intl
    - json
    - zip

3. **Update php.ini Settings**
    - Left-click WAMP icon → PHP → php.ini
    - Update these values:
   ```ini
   max_execution_time = 300
   memory_limit = 256M
   post_max_size = 50M
   upload_max_filesize = 50M
   ```

### Quick Start with WAMP

1. ✅ **Install WAMP** from https://www.wampserver.com/
2. ✅ **Start WAMP** (icon should be green)
3. ✅ **Open phpMyAdmin** (http://localhost/phpmyadmin)
4. ✅ **Create database** `agricloud`
5. ✅ **Import SQL** from `SQL_COMMANDS_GUIDE.txt`
6. ✅ **Verify tables** (should see 14 tables)

---

## Important Notes

1. **Security:**
    - Always hash passwords (bcrypt)
    - Use HTTPS in production
    - Implement JWT for API authentication
    - Validate all user inputs
    - Use prepared statements (prevent SQL injection)

2. **Performance:**
    - Use database indexes
    - Implement caching
    - Optimize image uploads
    - Lazy load large datasets

3. **Code Quality:**
    - Follow naming conventions
    - Write clean, readable code
    - Add comments for complex logic
    - Write unit tests
    - Use version control

4. **Collaboration:**
    - Daily standups
    - Code reviews
    - Use project management tool (Jira/Trello)
    - Document your work
    - Help team members

---

## Resources

- **Database Schema:** See `AgriCloud_Database_Schema.txt`
- **SQL Commands:** See `SQL_COMMANDS_GUIDE.txt`
- **User Stories:** See `AgriCloud_User_Stories_English.txt`
- **Full Documentation:** See `AgriCloud_Database_Documentation.pdf`

---

## Contact

For questions or issues, contact:
- **Farouk:** Module 1 (User Management)
- **Shahed:** Module 2 (Farm Management)
- **Ghada:** Module 3 (Market Management)
- **Rania:** Module 4 (Blog Management)
- **Ayman:** Module 5 (Event Management)

---

**Good Luck with Development! 🚀**
