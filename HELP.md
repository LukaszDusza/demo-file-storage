# Dokumentacja pierwszego uruchomienia aplikacji w Docker

---

## Wprowadzenie

Ta dokumentacja opisuje, jak uruchomić aplikację przy użyciu Dockera po raz pierwszy. Aplikacja została skonfigurowana do automatycznego budowania pliku JAR przy użyciu Mavena i uruchamiania w środowisku Java 21.

---

## Wymagania wstępne

1. **Docker i Docker Compose**:
    - Zainstaluj Dockera na swoim systemie.
    - Możesz pobrać i zainstalować Dockera z [oficjalnej strony Docker](https://www.docker.com/get-started).

2. **Kod źródłowy aplikacji**:
    - Kod źródłowy aplikacji z plikami `Dockerfile` i `pom.xml` oraz folderem `src`.

---

## Pierwsze uruchomienie

1. **Sklonowanie repozytorium**:
   Jeśli aplikacja znajduje się w repozytorium Git, sklonuj je:
   ```bash
   git clone https://github.com/username/repo.git
   cd repo
   ```

2. **Budowanie obrazu Dockera**:
   Użyj poniższego polecenia, aby zbudować obraz Dockera:
   ```bash
   docker build -t demo-file-storage .
   ```
    - `-t demo-file-storage`: Nadaje obrazowi nazwę `demo-file-storage`.

3. **Uruchamianie kontenera**:
   Uruchom kontener na porcie `8080`:
   ```bash
   docker run -p 8080:8080 demo-file-storage
   ```
    - `-p 8080:8080`: Mapuje port `8080` aplikacji w kontenerze na port `8080` hosta.

4. **Sprawdzenie działania aplikacji**:
    - Otwórz przeglądarkę internetową i przejdź pod adres: [http://localhost:8080](http://localhost:8080).
    - Powinna być dostępna aplikacja.

---

## Najczęstsze problemy i ich rozwiązania

1. **Brak Dockera**:
   Jeśli Docker nie jest zainstalowany, zainstaluj go zgodnie z [instrukcją](https://www.docker.com/get-started).

2. **Problemy z budowaniem obrazu**:
    - Sprawdź, czy plik `Dockerfile` jest poprawny i znajduje się w głównym katalogu projektu.
    - Upewnij się, że katalog `src` i plik `pom.xml` są obecne.

3. **Błąd: "Address already in use"**:
    - Port `8080` może być zajęty. Uruchom aplikację na innym porcie, np.:
      ```bash
      docker run -p 9090:8080 demo-file-storage
      ```

4. **Out of Memory Error**:
    - Jeśli aplikacja przetwarza duże pliki, możesz zwiększyć pamięć JVM, edytując zmienną `JAVA_OPTS` w Dockerfile:
      ```bash
      ENV JAVA_OPTS="-Xms512m -Xmx2g"
      ```

---

## Dodatkowe informacje

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

## Uwagi końcowe

Twoja aplikacja jest teraz gotowa do uruchomienia w Dockerze. Jeśli pojawią się pytania lub problemy, zapoznaj się z logami aplikacji za pomocą:
```bash
docker logs <CONTAINER_ID>
```

Ciesz się korzystaniem z aplikacji! 🎉