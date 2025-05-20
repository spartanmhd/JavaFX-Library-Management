# Library Management System

A **Library Management System** built with **JavaFX**, designed with **Scene Builder**, and powered by a **MySQL** database. This project follows the **MVC (Model-View-Controller)** pattern, providing a robust solution for managing books, members, and loans in a library setting. The application features both **Member** and **Admin** interfaces, including authentication, CRUD operations, search, sorting, and loan management.

---

## Features

### User Authentication
- **Member Login:** Secure login for registered members (username & password).
- **Registration:** New users can register with their details.
- **Admin Login:** Separate admin access for management functions.
- **Password Storage:** Passwords are stored in plain text (not recommended for production).

### Member Interface
- **Browse Books:** View available books with details (title, author, ISBN, publication year, status).
- **Search & Sort:** Filter books by title, author, or ISBN. Sort by any column.
- **Profile Page:** Update personal info and view borrowing history.
- **Loan Books:** Request book loans, select duration, and track current loans.

### Admin Interface
- **Book Management:** Add, update, delete, and view all books.
- **Member Management:** Add, update, delete, and view all members.
- **Loan Management:** Issue loans, mark returns, and view all borrowing history.

### Technical Highlights
- **JavaFX UI:** Designed with Scene Builder (FXML layouts).
- **MySQL Database:** phpMyAdmin for management; tables for books, members, and loans.
- **MVC Architecture:** Separation of logic, UI, and data access.
- **Advanced Styling:** Custom CSS for a modern, user-friendly interface.
- **Error Handling:** Validation for all forms and actions.
- **Code Comments:** Every class, method, and FXML file is clearly commented.

---

## Technologies Used

- **Java 11+**
- **JavaFX 17+**
- **Scene Builder** (for FXML design)
- **MySQL** (phpMyAdmin for DB management)
- **JDBC** (Java Database Connectivity)
- **MVC Architecture**
- **CSS** (for JavaFX styling)
- **IntelliJ IDEA** (recommended IDE)

---

## Database Schema

- **books**: `id`, `title`, `author`, `isbn`, `year`, `available`
- **members**: `id`, `username`, `password`, `name`, `contact`
- **loans**: `id`, `book_id`, `member_id`, `loan_date`, `return_date`, `status`

*See `/sql/schema.sql` for full database setup.*

---

## Getting Started

### Prerequisites

- Java JDK 11 or later
- JavaFX SDK
- Scene Builder
- MySQL Server & phpMyAdmin
- IntelliJ IDEA or Eclipse

### Setup Steps

1. **Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/library-management-system.git
   ```

2. **Import Project**
    - Open the project in IntelliJ IDEA or your preferred Java IDE.

3. **Configure the Database**
    - Create a MySQL database (e.g., `library_db`).
    - Import the schema from `/sql/schema.sql`.

4. **Update Database Configuration**
    - Edit the database connection settings in `src/main/resources/db.properties`:
      ```
      db.url=jdbc:mysql://localhost:3306/library_db
      db.user=your_mysql_user
      db.password=your_mysql_password
      ```

5. **Run the Application**
    - Use the IDE to run the `Main.java` class.
    - Use Scene Builder to modify FXML layouts if desired.

---

## Folder Structure

```
library-management-system/
├── src/
│   ├── controller/
│   ├── model/
│   ├── view/           # FXML files
│   ├── util/
│   └── Main.java
├── resources/
│   ├── css/
│   ├── db.properties
│   └── images/
├── sql/
│   └── schema.sql
└── README.md
```

---

## Notes

- **Security:** Passwords are stored in plain text for demonstration purposes (never do this in production).
- **Error Handling:** The application includes user-friendly error prompts and input validation.
- **Styling:** Advanced CSS is used for a modern look; you can customize styles in `/resources/css/`.
- **Extensibility:** The codebase is modular and ready for further enhancements (e.g., password hashing, REST API integration).

---

## Screenshots

_Add screenshots of your main interface, admin dashboard, and profile page here._

---

## License

This project is open-source and available under the MIT License.

---

**Happy Coding!**
