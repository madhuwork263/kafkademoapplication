package com.example.demo.service;

import com.example.demo.dto.WordCount;
import com.example.demo.job.WordCountJob;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class FlinkJobService {

    private final StreamExecutionEnvironment env;
    private final WordCountJob wordCountJob;

    public FlinkJobService(StreamExecutionEnvironment env, WordCountJob wordCountJob) {
        this.env = env;
        this.wordCountJob = wordCountJob;
    }

    public List<WordCount> runWordCountJob() {
        try {
            List<String> sampleData = Arrays.asList(
                    "Hello World",
                    "Hello Apache Flink",
                    "Flink is awesome",
                    "Spring Boot with Flink",
                    "Hello Spring Boot"
            );

            return wordCountJob.executeBatchWordCount(env, sampleData);
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute Flink job", e);
        }
    }

    public String runStreamingWordCount(String inputPath) {
        try {
            wordCountJob.executeStreamingWordCount(env, inputPath);
            env.execute("Streaming Word Count Job");
            return "Streaming job started successfully";
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute streaming Flink job", e);
        }
    }
}