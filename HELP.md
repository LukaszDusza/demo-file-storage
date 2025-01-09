
# Dokumentacja pierwszego uruchomienia aplikacji w Docker

---

## **Wprowadzenie**

Ta dokumentacja opisuje, jak uruchomić aplikację przy użyciu Dockera po raz pierwszy. Aplikacja została skonfigurowana do automatycznego budowania pliku JAR przy użyciu Mavena i uruchamiania w środowisku Java 21.

---

## **Wymagania wstępne**

1. **Docker i Docker Compose**:
    - Zainstaluj Dockera na swoim systemie.
    - Możesz pobrać i zainstalować Dockera z [oficjalnej strony Docker](https://www.docker.com/get-started).

2. **Kod źródłowy aplikacji**:
    - Kod źródłowy aplikacji z plikami `Dockerfile` i `pom.xml` oraz folderem `src`.

---

## **Pierwsze uruchomienie**

1. **Sklonowanie repozytorium**:
   Jeśli aplikacja znajduje się w repozytorium Git, sklonuj je:
   ```bash
   git clone https://github.com/LukaszDusza/demo-file-storage.git
   cd demo-file-storage
   ```

2. **Budowanie obrazu Dockera i uruchamianie aplikacji**:
   ```bash
   docker-compose up -d
   ```

3. **Sprawdzenie działania aplikacji**:
    - Otwórz przeglądarkę internetową i przejdź pod adres: [http://localhost:8080](http://localhost:8080).
    - Aplikacja powinna być dostępna.

---

## **Dostępne endpointy**

### **1. Przesyłanie plików**
- **Metoda:** `POST`
- **Endpoint:** `/api/v1/files/upload`
- **Opis:** Umożliwia przesyłanie jednego lub wielu plików w formacie `multipart/form-data`.
- **Przykładowe żądanie cURL:**
  ```bash
  curl -X POST http://localhost:8080/api/v1/files/upload        -H "Content-Type: multipart/form-data"        -F "files=@example.txt"
  ```
- **Odpowiedź:** Lista obiektów `FileMetadata` z metadanymi zapisanych plików.

### **1a. Przesyłanie plików**
- **Metoda:** `POST`
- **Endpoint:** `/api/v1/files/upload/input-stream`
- **Opis:** Umożliwia przesyłanie jednego lub wielu plików w formacie `multipart/form-data`.
- **Przykładowe żądanie cURL:**
  ```bash
  curl -X POST http://localhost:8080/api/v1/files/upload/input-stream        -H "Content-Type: multipart/form-data"        -F "files=@example.txt"
  ```
- **Odpowiedź:** Lista obiektów `FileMetadata` z metadanymi zapisanych plików.

### **2. Pobranie wszystkich plików**
- **Metoda:** `GET`
- **Endpoint:** `/api/v1/files`
- **Opis:** Zwraca listę wszystkich plików zapisanych w bazie danych.
- **Przykładowe żądanie cURL:**
  ```bash
  curl -X GET http://localhost:8080/api/v1/files
  ```
- **Odpowiedź:** Lista obiektów `FileMetadata`.

### **3. Pobranie pliku po ID**
- **Metoda:** `GET`
- **Endpoint:** `/api/v1/files/{id}`
- **Opis:** Pobiera metadane pliku na podstawie jego ID.
- **Przykładowe żądanie cURL:**
  ```bash
  curl -X GET http://localhost:8080/api/v1/files/1
  ```
- **Odpowiedź:** Obiekt `FileMetadata` zawierający dane pliku.

### **4. Pobranie pliku po nazwie**
- **Metoda:** `GET`
- **Endpoint:** `/api/v1/files/by-name?fileName={fileName}`
- **Opis:** Pobiera metadane pliku na podstawie jego nazwy.
- **Przykładowe żądanie cURL:**
  ```bash
  curl -X GET "http://localhost:8080/api/v1/files/by-name?fileName=example.txt"
  ```
- **Odpowiedź:** Obiekt `FileMetadata` zawierający dane pliku.

---

## **Najczęstsze problemy i ich rozwiązania**

1. **Brak Dockera**:
   Jeśli Docker nie jest zainstalowany, zainstaluj go zgodnie z [instrukcją](https://www.docker.com/get-started).

2. **Problemy z budowaniem obrazu**:
    - Sprawdź, czy plik `Dockerfile` jest poprawny i znajduje się w głównym katalogu projektu.
    - Upewnij się, że katalog `src` i plik `pom.xml` są obecne.

3. **Błąd: "Address already in use"**:
    - Port `8080` może być zajęty. Uruchom aplikację na innym porcie, np.:
      ```bash
      docker-compose up -d --build -p 9090:8080
      ```

4. **Out of Memory Error**:
    - Jeśli aplikacja przetwarza duże pliki, możesz zwiększyć pamięć JVM, edytując zmienną `JAVA_OPTS` w Dockerfile:
      ```bash
      ENV JAVA_OPTS="-Xms512m -Xmx2g"
      ```

---

## **Dodatkowe informacje**

- **Zatrzymanie kontenera**:
  Aby zatrzymać uruchomioną aplikację, użyj:
  ```bash
  docker ps
  docker stop <CONTAINER_ID>
  ```

- **Ponowne uruchomienie kontenera**:
  Możesz ponownie uruchomić aplikację za pomocą:
  ```bash
  docker start <CONTAINER_ID>
  ```

- **Usuwanie kontenera**:
  Jeśli chcesz usunąć kontener, wykonaj:
  ```bash
  docker rm <CONTAINER_ID>
  ```

---

## **Uwagi końcowe**

Twoja aplikacja jest teraz gotowa do uruchomienia w Dockerze. Jeśli pojawią się pytania lub problemy, zapoznaj się z logami aplikacji za pomocą:
```bash
docker logs <CONTAINER_ID>
```

Ciesz się korzystaniem z aplikacji! 🎉
