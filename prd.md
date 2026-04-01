# 📄 Dokumen Kebutuhan Produk (PRD)

---

**Nama Aplikasi (Saran):** BandingHarga

**Versi:** 1.0 (MVP - Minimum Viable Product)

**Platform:** Android Native

**Tech Stack:** Kotlin, Jetpack Compose, ViewModel (Arsitektur MVVM), Firebase Analytics.

**Fokus Utama:** Input Manual, Perbandingan Harga Online vs Offline, 100% Offline Support.

## **1. Ringkasan Eksekutif**

Banyak konsumen kebingungan menentukan opsi mana yang paling menguntungkan: membeli barang di *e-commerce* (dengan tambahan ongkir dan potongan voucher) atau membelinya langsung di toko fisik/mall (dengan tambahan biaya bensin/parkir). Aplikasi ini hadir sebagai utilitas kalkulator pintar yang memproses variabel-variabel tersebut secara instan untuk menentukan toko mana yang memberikan harga akhir paling murah (paling *worth it*).

## **2. Tujuan Produk**

- **Solusi Pengguna:** Memberikan hasil perbandingan harga yang akurat dan objektif dalam hitungan detik.
- **Fokus MVP:** Membangun aplikasi yang ringan, responsif, dan bisa berjalan tanpa koneksi internet (kecuali untuk memuat iklan).
- **Monetisasi:** Menghasilkan pendapatan pasif dari Google AdMob melalui penempatan iklan yang strategis dan tidak merusak pengalaman pengguna (UX).

## **3. Target Pengguna**

- Pengguna *marketplace* (Shopee, Tokopedia, dll) yang ingin menghitung harga asli setelah ditambah ongkir dan dikurangi *cashback*/diskon.
- Pembeli yang sedang berada di mall dan ingin membandingkan harga barang di depannya dengan harga di toko *online*.

## **4. Kebutuhan Fitur (Functional Requirements)**

### **A. Modul Input (Layar Formulir)**

Pengguna dapat menambahkan dua atau lebih "Kartu Toko" untuk dibandingkan. Di dalam Compose, ini bisa dibuat sebagai *Reusable Composable Function*. Terdapat dua mode input:

**Mode 1: Toko Online**

- **Nama Platform:** (Teks) cth: *Toko Sepatu A - Shopee*
- **Harga Barang:** (Angka) cth: *150.000*
- **Ongkos Kirim:** (Angka) cth: *15.000*
- **Diskon/Voucher:** (Angka) cth: *10.000*

**Mode 2: Toko Offline (Fisik)**

- **Nama Toko:** (Teks) cth: *Toko Olahraga Mall X*
- **Harga Barang:** (Angka) cth: *145.000*
- **Biaya Transport/Parkir:** (Angka) cth: *10.000*
- **Diskon Toko:** (Angka) cth: *0*

### **B. Mesin Kalkulasi (ViewModel Logic)**

- **Rumus Dasar:** (Harga Barang + Biaya Tambahan) - Diskon = **Harga Akhir**
- Logika ini harus dipisahkan dari UI (disimpan di dalam ViewModel) agar aplikasi tidak kehilangan data saat layar HP diputar (screen rotation).

### **C. Layar Hasil (Result Screen)**

- Menampilkan daftar toko yang sudah diurutkan dari Harga Akhir termurah ke termahal.
- Toko termurah mendapat *highlight* visual (misalnya *Card* berwarna emas atau ikon trofi 🏆).
- Menampilkan ringkasan selisih harga (cth: *"Lebih hemat Rp5.000 dibanding Toko B"*).

## **5. Alur Pengguna (User Flow)**

1. **Layar Utama:** Layar kosong dengan tombol **"Tambah Toko"** (Floating Action Button).
2. **Input Data:** Pengguna mengisi data Toko 1 (Online) dan menyimpannya. Muncul *Card* Toko 1.
3. **Input Data 2:** Pengguna menekan tambah lagi untuk Toko 2 (Offline).
4. **Eksekusi:** Pengguna menekan tombol utama **"Bandingkan Harga"**.
5. **Jeda Iklan:** Iklan layar penuh (*Interstitial Ad*) muncul.
6. **Hasil:** Pengguna melihat kartu pemenang dan rincian perhitungannya.
7. **Reset:** Tombol "Hitung Ulang" untuk mengosongkan layar dan memulai sesi baru.

## **6. Arahan UI/UX (Khusus Jetpack Compose)**

- **Material Design 3:** Gunakan komponen bawaan Material 3 dari Compose (seperti ElevatedCard, OutlinedTextField) agar tampilan terlihat modern secara *default*.
- **Keyboard Pintar:** Saat kursor masuk ke kolom Harga/Ongkir/Diskon, pastikan yang muncul adalah **Numpad** (keyboard angka), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number). Ini memangkas waktu input secara drastis.
- **Indikator Warna:** Gunakan warna hijau untuk teks "Diskon" (pengurang harga) dan teks merah untuk "Ongkir/Parkir" (penambah harga).

## **7. Strategi AdMob**

- **Banner Ads:** Letakkan di bagian paling bawah antarmuka (Scaffold bottomBar) agar selalu terlihat namun tidak menghalangi tombol utama.
- **Interstitial Ads:** Dimunculkan pada langkah ke-5 (saat pengguna menekan "Bandingkan Harga"). *Timing* ini krusial karena pengguna sedang penasaran dengan hasilnya, sehingga tingkat penutupan paksa (*bounce rate*) iklan akan sangat rendah.

## **8. Metrik Pelacakan (Firebase Analytics)**

Integrasikan Firebase SDK dasar untuk memantau:

- **Event Click:** Seberapa sering tombol "Bandingkan" ditekan (mengukur interaksi inti).
- **Mode Preference:** Apakah pengguna lebih sering membuat kartu "Toko Online" atau "Toko Offline".
- **Session Length:** Rata-rata waktu yang dihabiskan pengguna. Idealnya singkat (1-2 menit), yang berarti utilitas Anda berfungsi efisien.