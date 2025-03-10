# **Cloud-Based Storage Application** ☁️💾

A **Java-based Cloud Storage System** that enables users to upload, store, and share files securely over the internet. This system uses **Java Threads, TCP/UDP protocols, and Synchronization** for seamless file handling across multiple clients and a centralized server.

## **📂 Project Structure**

This repository contains the following folders:

```
📂 clientFileHolder      -> Storage directory for Client 1
📂 clientFileHolder2     -> Storage directory for Client 2
📂 serverFileHolder      -> Storage directory for Server
📂 src                   -> Contains all Java source files
```

It also includes test files for checking system functionality.

---

## **🔧 Setup Instructions**

Before running the application, update file paths in the `Constants.java` file inside the `src` folder.

### 1️⃣ Update File Paths in  **`Constants.java`**

Modify these **3 lines** inside `src/Constants.java` by replacing them with absolute paths:

```java
public static final String CLIENT_FILE_ROOT = "absolute/path/to/clientFileHolder";
public static final String CLIENT_FILE_ROOT2 = "absolute/path/to/clientFileHolder2";
public static final String SERVER_FILE_ROOT = "absolute/path/to/serverFileHolder";
```

📌 **To find the absolute path**: Right-click on each folder > Copy Absolute Path.

---

## **🚀 How to Run**

### **Navigate to the ****`src`**** Directory in Terminal**

```sh
cd path/to/src
```

### **2️⃣ Compile the Java Files**

```sh
javac *.java
```

### **3️⃣ Run the Application (Multiple Terminals Required)**

#### **Open 3 terminals and execute the following:**

1️⃣ **Run the Server** (Terminal 1)

```sh
java Server
```

2️⃣ **Run the First Client** (Terminal 2)

```sh
java Client
# Enter '1' when prompted
```

3️⃣ **Run the Second Client** (Terminal 3)

```sh
java Client
# Enter '2' when prompted
```

---

## **📌 Features**

✅ Secure file upload and storage\
✅ Multi-client access with concurrent handling\
✅ Java TCP/UDP for network communication\
✅ Thread synchronization for consistency\
✅ Dynamic file path configurations

---

## **📜 Notes**

- Ensure **Java 8+** is installed on your system.
- If facing permission issues, try running:
  ```sh
  chmod +x *.java
  ```
- The project follows **client-server architecture** with two clients and a shared server.

---

