package com.hyundai.test.address.common;

import com.hyundai.test.address.dao.AddressBookDao;
import com.hyundai.test.address.dao.SequenceDao;
import com.hyundai.test.address.util.MessageUtil;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class CsvFileWriter {

    private final MessageUtil messageUtil;
    private final AddressBookDao addressBookDao;
    private final SequenceDao sequenceDao;

    @PreDestroy
    public void saveDataOnShutdown() {
        String addressCsvPath = "src/main/resources/csv/address.csv";
        String sequenceCsvPath = "src/main/resources/csv/sequence.csv";
        String saveMessage = "변경된 데이터가 없어 저장하지 않습니다.";
        if (!addressBookDao.toCsvLines().equals(addressBookDao.getAddressBook_init())) {
            backupAndOverwriteCsv(addressCsvPath, addressBookDao.toCsvLines());
            overwriteCsv(sequenceCsvPath, sequenceDao.toCsvLines());
            saveMessage = "데이터가 CSV 파일에 저장되었습니다.";
        }
        System.out.println("\n**************************************************");
        System.out.println("   [프로그램 종료] " + saveMessage);
        System.out.println("**************************************************\n");
    }


    public void overwriteCsv(String filename, List<String> lines) {
        try (BufferedWriter writer = Files.newBufferedWriter(
                Paths.get(filename), StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            log.info("{}: {}", messageUtil.getMessage("log.csv.write.success"), filename);
        } catch (IOException e) {
            log.error("{}: {}", messageUtil.getMessage("log.csv.write.fail"), filename, e);
            throw new RuntimeException(messageUtil.getMessage("log.csv.write.fail"), e);
        }
    }

    public void backupAndOverwriteCsv(String filename, List<String> lines) {
        try {
            Path originPath = Paths.get(filename);
            String backupDir = "src/main/resources/csv/address_backup";
            Files.createDirectories(Paths.get(backupDir));
            String baseFileName = Paths.get(filename).getFileName().toString();
            String backupFile = backupDir + "/" + baseFileName
                    + ".bak_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            Path backupPath = Paths.get(backupFile);

            if (Files.exists(originPath)) {
                Files.copy(originPath, backupPath, StandardCopyOption.REPLACE_EXISTING);
                log.info("{}: {}", messageUtil.getMessage("log.csv.backup.success"), backupFile);
            }

            overwriteCsv(filename, lines);

        } catch (IOException e) {
            log.error("{}: {}", messageUtil.getMessage("log.csv.backup.fail"), filename, e);
            throw new RuntimeException(messageUtil.getMessage("log.csv.backup.fail"), e);
        }
    }
}
