package it.polimi.nsds.spark.eval;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.apache.spark.sql.functions.*;

/**
 * Group number: 12
 * Group members:
 * member 1 Laura Colazzo
 * member 2 Alessandro Meroni
 * member 3 Filippo Giovanni Del Nero
 */
public class SparkGroup12 {
    public static void main(String[] args) throws TimeoutException {
        final String master = args.length > 0 ? args[0] : "local[4]";
        final String filePath = args.length > 1 ? args[1] : "./";

        final SparkSession spark = SparkSession
                .builder()
                .master(master)
                .appName("SparkEval")
                .getOrCreate();
        spark.sparkContext().setLogLevel("ERROR");

        final List<StructField> citiesRegionsFields = new ArrayList<>();
        citiesRegionsFields.add(DataTypes.createStructField("city", DataTypes.StringType, false));
        citiesRegionsFields.add(DataTypes.createStructField("region", DataTypes.StringType, false));
        final StructType citiesRegionsSchema = DataTypes.createStructType(citiesRegionsFields);

        final List<StructField> citiesPopulationFields = new ArrayList<>();
        citiesPopulationFields.add(DataTypes.createStructField("id", DataTypes.IntegerType, false));
        citiesPopulationFields.add(DataTypes.createStructField("city", DataTypes.StringType, false));
        citiesPopulationFields.add(DataTypes.createStructField("population", DataTypes.IntegerType, false));
        final StructType citiesPopulationSchema = DataTypes.createStructType(citiesPopulationFields);

        final Dataset<Row> citiesPopulation = spark
                .read()
                .option("header", "true")
                .option("delimiter", ";")
                .schema(citiesPopulationSchema)
                .csv(filePath + "files/cities_population.csv");

        final Dataset<Row> citiesRegions = spark
                .read()
                .option("header", "true")
                .option("delimiter", ";")
                .schema(citiesRegionsSchema)
                .csv(filePath + "files/cities_regions.csv");

        //Q1
        final Dataset<Row> q1 = citiesPopulation
                .join(citiesRegions, "city")
                .groupBy("region")
                .sum("population")
                .withColumnRenamed("sum(population)", "totalPopulation")
                .select("region", "totalPopulation");

        q1.show();

        //Q2
        final Dataset<Row> q2 = citiesPopulation
                .join(citiesRegions, "city")
                .groupBy("region")
                .agg(max("population"), count("city"));

        q2.show();

        //Q3
        // JavaRDD where each element is an integer and represents the population of a city
        JavaRDD<Integer> population = citiesPopulation.toJavaRDD().map(r -> r.getInt(2));

        final int threshold = 100000000;
        int year = 1;
        int totalPopulation = sum(population);

        while(totalPopulation < threshold) {
            population = population.map(x -> x <= 1000 ? (int) (x*0.99) : (int) (x*1.01));
            population.cache();
            totalPopulation = sum(population);
            System.out.println("Year: " + year + " total population: " + totalPopulation);
            year++;
        }

        //Q4
        // Bookings: the value represents the city of the booking
        final Dataset<Row> bookings = spark
                .readStream()
                .format("rate")
                .option("rowsPerSecond", 100)
                .load();

        //messages can arrive up to 1 hour late and still be included in the window
        bookings.withWatermark("timestamp", "1 hour");

        final StreamingQuery q4 = bookings
                .join(citiesPopulation, bookings.col("value").equalTo(citiesPopulation.col("id")))
                .join(citiesRegions, "city")
                .groupBy(
                        col("region"),
                        window(col("timestamp"), "30 seconds", "5 seconds")
                )
                .count()
                .writeStream()
                .outputMode("update")
                .format("console")
                .start();

        try {
            q4.awaitTermination();
        } catch (final StreamingQueryException e) {
            e.printStackTrace();
        }

        spark.close();
    }

    private static int sum(JavaRDD<Integer> population) {
        return population.reduce(Integer::sum);
    }

}