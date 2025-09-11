# GitHub Analyzer Application

GitHub Analyzer, Apache organizasyonunun GitHub üzerindeki son güncellenmiş 100 reposunu analiz eder ve en çok yıldız almış 5 repo ile bu repolardaki en çok katkıda bulunan 10 kullanıcıyı toplar. Elde edilen veriler, H2 veritabanına kaydedilir.

## 🧩 Özellikler

- Tekrar veri kaydını önleme (duplicate önlemi)
- Spring Boot ile REST endpoint ile manuel ve otomatik veri çekme desteği
- Unit testler ile güvenilirlik

## 📦 Teknolojiler

- Java 11
- Spring Boot 2.7.18
- Spring Data JPA
- H2 Database (in-memory)
- RestTemplate
- JUnit 5, Mockito

## 🧪 Test Kapsamı

### ✔️ Unit Testler

Aşağıda unit test verilmiştir. Projede yer alan testler, hem başarı senaryolarını hem de olası uç durumları (edge cases) kapsamaktadır.


#### 🔹 `GitHubServiceTest`

- 100 sahte repo arasında yıldız sayısına göre ilk 5 tanesinin doğru şekilde kaydedildiği test edilir.
- Bu 5 repo için her birinden 10 sahte katkıcının bilgisi çekilip veritabanına eklendiği test edilir.
- Kullanıcı verileri ve katkı sayıları doğrulanır.

#### 🔹 `GitHubServiceEdgeCasesTest`

Uç senaryolar (edge cases) test edilmiştir:

- Repository listesi `null` olduğunda işlem yapılmadığı test edilir.
- Repository listesi boş olduğunda işlem yapılmadığı test edilir.
- Contributors listesi `null` olduğunda sadece repo'nun kaydedildiği test edilir.
- Bir katkıcının daha önce aynı repo için kaydedilmiş olması durumunda tekrar eklenmediği test edilir.

#### 🔹 `GitHubControllerTest`
- /fetch endpoint’ine gelen istek GitHubService.fetchAndSaveData() metodunu çağırıyor mu test edilir.

#### 🔹 `RepositoryRepositoryTest`

-  @DataJpaTest ile Repository katmanı test edilir, gerçek H2 memory DB üzerinde çalışır.

#### 🔹 `ContributorRepositoryTest`

- JPA ile Contributor kaydı ve existsBy... sorgusu test edilir.


## 🚀 Nasıl Çalıştırılır?

Bağımlılıkları Yükle (`mvn clean install`)

### 1. Adım
`GitHubAnalyzerApplication`'ı run et. (5-6 sn bekleme süresi vardır. Sql işlemlerinden dolayı)

Uygulama başladığında otomatik olarak GitHub API’den verileri çeker ve veritabanına kaydeder. Çıktı terminalde görünür.

### 2. Adım
`GitHubAnalyzerApplication'ı run ettikten sonra`;  

http://localhost:8080/fetch e istek gönder.  Bu endpoint’e GET isteği göndererek verileri manuel de çekmiş olacaksınız.
GitHub API’den veri çekilip veritabanına kaydedilecek ve tarayıcıda "GitHub data fetched and saved successfully!" mesajı dönecektir. Terminalde proje çıktısını görebilirsiniz.   

http://localhost:8080/h2-console a istek göndererek de H2 veritabanına bağlanmak için gereken bağlantı ayarları görünecektir.

### 3. Adım

`run.bat` dosyasını çalıştır. 

Uygulama başladığında otomatik olarak GitHub API’den verileri çeker ve veritabanına kaydeder. Çıktı terminalde görünür. Aynı zamanda `output.txt` dosyasında çıktı kaydedilir.



##  🤩 Örnek Çıktı

=== Repositories ===    
repo: dubbo, stars: 41151   
repo: echarts, stars: 63986     
repo: spark, stars: 41455   
repo: airflow, stars: 40997     
repo: superset, stars: 67099
    
=== Contributors ===    
repo: superset, user: mistercrunch, location: San Mateo, CA, company: preset-io, contributions: 2326    
repo: superset, user: dependabot[bot], location: null, company: null, contributions: 950    
repo: superset, user: kristw, location: San Francisco, CA, company: @airbnb , contributions: 834    
repo: superset, user: villebro, location: Palo Alto, California, company: null, contributions: 827
.       
.       
.       
.
