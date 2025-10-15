package com.example.demo.job;

import com.example.demo.dto.WordCount;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.connector.file.src.reader.TextLineInputFormat;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
public class WordCountJob {

    public List<WordCount> executeBatchWordCount(StreamExecutionEnvironment env, List<String> words) throws Exception {
        List<WordCount> results = new ArrayList<>();

        DataStream<String> textLines = env.fromCollection(words);

        DataStream<WordCount> wordCounts = textLines
                .flatMap(new Tokenizer())
                .keyBy(value -> value.f0)
                .sum(1)
                .map(new MapFunction<Tuple2<String, Integer>, WordCount>() {
                    @Override
                    public WordCount map(Tuple2<String, Integer> value) throws Exception {
                        return new WordCount(value.f0, value.f1);
                    }
                });

        // Collect results (for demonstration - in production you'd write to a sink)
        wordCounts.executeAndCollect().forEachRemaining(results::add);

        return results;
    }

    public void executeStreamingWordCount(StreamExecutionEnvironment env, String inputPath) throws Exception {
        // File source for streaming (if you have files to process)
        FileSource<String> source = FileSource
                .forRecordStreamFormat(new TextLineInputFormat(), new Path(inputPath))
                .monitorContinuously(Duration.ofSeconds(1))
                .build();

        DataStream<String> textLines = env.fromSource(source,
                WatermarkStrategy.noWatermarks(), "file-source");

        DataStream<WordCount> wordCounts = textLines
                .flatMap(new Tokenizer())
                .keyBy(value -> value.f0)
                .window(TumblingProcessingTimeWindows.of(Time.seconds(5)))
                .sum(1)
                .map(new MapFunction<Tuple2<String, Integer>, WordCount>() {
                    @Override
                    public WordCount map(Tuple2<String, Integer> value) throws Exception {
                        return new WordCount(value.f0, value.f1);
                    }
                });

        // Print results (in production, use proper sinks like Kafka, Database, etc.)
        wordCounts.print();
    }

    public static class Tokenizer implements FlatMapFunction<String, Tuple2<String, Integer>> {
        @Override
        public void flatMap(String value, Collector<Tuple2<String, Integer>> out) {
            String[] tokens = value.toLowerCase().split("\\W+");
            for (String token : tokens) {
                if (token.length() > 0) {
                    out.collect(new Tuple2<>(token, 1));
                }
            }
        }
    }
}