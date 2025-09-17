# Analyzer Application

Analyzer, Apache organizasyonunun GitHub üzerindeki son güncellenmiş 100 reposunu analiz eder ve en çok yıldız almış 5 repo ile bu repolardaki en çok katkıda bulunan 10 kullanıcıyı toplar. Elde edilen veriler, H2 veritabanına kaydedilir.

## 🧩 Özellikler

- Tekrar veri kaydını önleme (duplicate önlemi)
- Spring Boot ile REST endpoint ile manuel ve otomatik veri çekme desteği
- Unit testler ile güvenilirlik

## 🚀 Nasıl Çalıştırılır?

Bağımlılıkları Yükle (`mvn clean install`)

### 1. Adım
`GitHubAnalyzerApplication'ı run et.` (5-6 sn bekleme süresi vardır. Sql işlemlerinden dolayı)

Uygulama başladığında otomatik olarak GitHub API’den verileri çeker ve veritabanına kaydeder. Çıktı output.txt de görünür.

### 2. Adım
`GitHubAnalyzerApplication'ı run ettikten sonra  
http://localhost:8080/fetch e istek gönder.` 

Bu endpoint’e GET isteği gönderilerek veriler manuel çekilmiş olur 
ve JSON formatında repository döner.

http://localhost:8080/contributors a istek göndererek contributor JSON formatında listelenir

### 3. Adım

`run.bat` dosyasını çalıştır. 

Uygulama başladığında otomatik olarak GitHub API’den verileri çeker ve veritabanına kaydeder. Çıktı `output.txt` dosyasında kaydedilir.

