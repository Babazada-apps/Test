# Uçuş İstismarı üzrə Təlimat – Test (Android)

Mi-17-1V üzrə offline test, imtahan rejimi, statistika, seçilmişlər və axtarış tətbiqi.

## APK-nı GitHub-da qurmaq
1. Bu qovluğu yeni GitHub repozitoriyasına yükləyin (`main` branch).
2. **Actions → Build APK** işə düşəcək (və ya "Run workflow" ilə əl ilə işə salın).
3. İş bitəndə həmin run-un səhifəsində **Artifacts → ucus-istismari-test-debug-apk** bölməsindən APK-nı endirin.
4. Telefonda "naməlum mənbələrdən quraşdırma"ya icazə verib APK-nı açın.

## Yerli qurmaq
Android Studio-da qovluğu açın və Run edin (JDK 17, Gradle 9.3.1+).

## Sual bazası
Suallar `app/src/main/assets/questions.json` faylındadır. ID-lər massivdəki sıraya görə verilir:
yeni sualları YALNIZ faylın sonuna əlavə edin və `InitialData.SEED_VERSION` dəyərini artırın.
Mövcud suallar yenilənəndə istifadəçinin seçilmişləri və statistikası qorunur.
