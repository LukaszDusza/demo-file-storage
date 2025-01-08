
# File Processing Service

## Opis projektu
Aplikacja to serwis do przetwarzania plików, który:
- Odczytuje przesyłane pliki.
- Oblicza ich sumy kontrolne (algorytm SHA-256).
- Zapisuje metadane plików w bazie danych (nazwa, suma kontrolna, rozmiar).
- Przekazuje zawartość plików do zewnętrznego serwisu przy użyciu interfejsu `StorageService`.

Serwis został zaimplementowany w technologii **Spring WebFlux** z wykorzystaniem **`ByteBuffer`** do przetwarzania danych.

---

## Zalety użycia `ByteBuffer` zamiast `InputStream`

Decyzja o użyciu `ByteBuffer` w metodzie `processFile` wynika z następujących korzyści:

### 1. **Wydajność i oszczędność pamięci**
- `ByteBuffer` umożliwia operacje bezpośrednie na pamięci, co znacząco poprawia wydajność podczas przetwarzania dużych plików.
- Operacje są bardziej pamięciooszczędne niż w przypadku `InputStream`, który wymaga dodatkowych buforów.

### 2. **Wsparcie dla nieblokującego I/O**
- `ByteBuffer` doskonale integruje się z architekturą reaktywną, taką jak Spring WebFlux, dzięki czemu:
    - Odczyt i przetwarzanie danych odbywają się w sposób asynchroniczny i nieblokujący.
    - Unika się problemów z blokowaniem wątków, co jest kluczowe dla skalowalności aplikacji.

### 3. **Łatwość manipulacji danymi**
- `ByteBuffer` oferuje wsparcie dla operacji na danych, takich jak:
    - Przechowywanie różnych typów danych (`getInt()`, `putFloat()` itp.).
    - Zarządzanie wskaźnikami pozycji (`position`), limitu (`limit`) i zdolności (`capacity`).
- To czyni go idealnym wyborem dla aplikacji wymagających elastyczności i kontroli.

### 4. **Przewidywalność i ponowne wykorzystanie danych**
- `ByteBuffer` działa całkowicie w pamięci, co sprawia, że jego zachowanie jest przewidywalne, w przeciwieństwie do `InputStream`, który zależy od źródła danych.
- Operacje takie jak `rewind()` lub `flip()` pozwalają wielokrotnie przetwarzać te same dane bez konieczności ich ponownego wczytywania.

### 5. **Naturalna integracja z nowoczesnymi bibliotekami**
- `ByteBuffer` jest wspierany przez wiele współczesnych bibliotek (np. Netty, Reactor), co upraszcza integrację z innymi komponentami aplikacji.

---

## Struktura projektu

```plaintext
src/main/java/com/demo/filesorage
├── controller
│   └── FileController.java       # Obsługuje żądania HTTP
├── model
│   └── FileMetadata.java         # Klasa reprezentująca metadane pliku
├── repository
│   └── FileMetadataRepository.java # Repozytorium do interakcji z bazą danych
├── service
│   └── FileService.java          # Logika biznesowa przetwarzania plików
├── storage
│   └── StorageService.java       # Interfejs do zewnętrznego zapisu plików
```

---

## Endpointy REST API

### 1. **Przesyłanie plików**
- **Metoda:** `POST`
- **Endpoint:** `/files/upload`
- **Opis:** Przetwarza przesłane pliki, oblicza ich sumy kontrolne, zapisuje metadane w bazie danych oraz przekazuje pliki do `StorageService`.

### 2. **Pobieranie wszystkich plików**
- **Metoda:** `GET`
- **Endpoint:** `/files`
- **Opis:** Zwraca listę wszystkich plików zapisanych w bazie danych.

### 3. **Pobieranie pliku po ID**
- **Metoda:** `GET`
- **Endpoint:** `/files/{id}`
- **Opis:** Zwraca metadane pliku na podstawie jego ID.

### 4. **Pobieranie pliku po nazwie**
- **Metoda:** `GET`
- **Endpoint:** `/files/by-name`
- **Parametry:** `fileName` (query parameter)
- **Opis:** Zwraca metadane pliku na podstawie jego nazwy.

---
