## C4 Model of the Current Architecture

### Context Diagram
![Context Diagram](/img/context-diagram.png)

### Container Diagram
![Container Diagram](/img/container-diagram.png)

### Deployment Diagram
![Deployment Diagram](/img/deployment-diagram.png)

## Future Architecture

### Future Context Diagram
![Future Context Diagram](/img/future-context-diagram.png)

### Future Container Diagram
![Future Container Diagram](/img/future-container-diagram.png)

## Risk Storming
![Risk Storming](/img/risk-storming.png)

Backend Spring Boot menjadi salah satu risiko utama karena menangani hampir seluruh proses bisnis aplikasi. Jika backend bermasalah, sebagian besar fitur dapat ikut terganggu. Mitigasinya adalah menjaga struktur backend tetap modular, menambahkan logging, health check, dan error handling agar masalah lebih mudah dideteksi dan diperbaiki.

Pada database PostgreSQL/Supabase, risikonya adalah database menjadi single source of truth. Jika database down atau schema berubah tanpa kontrol, data aplikasi bisa terganggu. Mitigasinya adalah backup rutin, penggunaan migration tool seperti Flyway atau Liquibase, serta konfigurasi akses database yang aman.

Risiko lain ada pada penyimpanan bukti foto dan monitoring sistem. Jika file tidak disimpan dengan baik, laporan kerja menjadi tidak lengkap, sementara kurangnya monitoring dapat membuat error atau penyalahgunaan akses terlambat diketahui. Mitigasinya adalah menggunakan object storage seperti Supabase Storage atau S3, membatasi akses file, serta menambahkan centralized logging, audit log, health endpoint, dan alert sederhana untuk masalah penting.

## Individual Component Diagram
Autentikasi Diagram:
![]()

Manajemen Kebun Diagram:
![Component Diagram](/img/kebun/component-diagram.png)

Manajemen Hasil Diagram: 
![Component Diagram](/img/hasil/componentdiagram.png)

Manajemen Pengiriman Diagram: 
![Component Diagram](/img/pengiriman/component.png)

Manajemen Pembayaran Diagram: 
![Component Diagram Pemnbayaran](img/pembayaran/componentDiagram.png)

## Individual Code Diagram
Autentikasi Diagram:
![]()

Manajemen Kebun Diagram: 
![Code Diagram](/img/kebun/code-diagram.png)

Manajemen Hasil Diagram: 
![Code Diagram](/img/hasil/codediagram.png)

Manajemen Pengiriman Diagram: 
![Code Diagram API](/img/pengiriman/apijs.png)
![Code Diagram Mandor](/img/pengiriman/mandorTab.png)
![Code Diagram Pengiriman API](/img/pengiriman/pengirimanAPI.png)
![Code Diagram Controller Supir Truk](/img/pengiriman/supirTrukController.png)


Manajemen Pembayaran Diagram: 
![Code Diagram Layered Architecture & Data Flow](/img/pembayaran/codeDiagramArchitecture.png)
![Code Diagram Strategy Pattern](/img/pembayaran/codeDiagramStrategyPattern.png)
![Code Diagram State Pattern](/img/pembayaran/codeDiagramStatePattern.png)