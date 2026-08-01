package com.breakingchains.config;

import com.breakingchains.model.CheckInStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class DatabaseConstraintFixer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        try {
            String allowedStatuses = Arrays.stream(CheckInStatus.values())
                    .map(status -> "'" + status.name() + "'")
                    .collect(Collectors.joining(", "));

            jdbcTemplate.execute("ALTER TABLE log_entries DROP CONSTRAINT IF EXISTS log_entries_status_check");
            jdbcTemplate.execute("ALTER TABLE log_entries ADD CONSTRAINT log_entries_status_check CHECK (status IN (" + allowedStatuses + "))");
            log.info("Successfully synced log_entries_status_check constraint with CheckInStatus enum values: [{}]", allowedStatuses);
        } catch (Exception e) {
            log.warn("Could not update log_entries_status_check constraint automatically: {}", e.getMessage());
        }
    }
}
