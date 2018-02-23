package cn.mlb.mongo.demo;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import cn.mlb.mongo.pojo.Address;
import cn.mlb.mongo.pojo.Person;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

public class MongoPojo {
	public static void main(String[] args) {
		try {
			// 连接到MongoDB服务 如果是远程连接可以替换“localhost”为服务器所在IP地址
			// ServerAddress()两个参数分别为 服务器地址 和 端口
			ServerAddress serverAddress = new ServerAddress("localhost", 27017);
			List<ServerAddress> addrs = new ArrayList<ServerAddress>();
			addrs.add(serverAddress);

			// MongoCredential.createScramSha1Credential()三个参数分别为 用户名 数据库名称 密码
			MongoCredential credential = MongoCredential
					.createScramSha1Credential("user", "account",
							"pwd".toCharArray());
			List<MongoCredential> credentials = new ArrayList<MongoCredential>();
			credentials.add(credential);

			// 通过连接认证获取MongoDB连接
			CodecRegistry pojoCodecRegistry = fromRegistries(
					MongoClient.getDefaultCodecRegistry(),
					fromProviders(PojoCodecProvider.builder().automatic(true)
							.build()));
			MongoClientOptions options = MongoClientOptions.builder()
					.codecRegistry(pojoCodecRegistry).build();
			MongoClient mongoClient = new MongoClient(addrs, credentials,
					options);
			// 连接到数据库
			MongoDatabase database = mongoClient.getDatabase("account");
			//mongoDatabase = mongoDatabase.withCodecRegistry(pojoCodecRegistry);
			System.out.println("Connect to database successfully");
			
			//insertOne(database);
			//insertMany(database);
			//findAll(database);
			//findOne(database);
			//updateOne(database);
			//updateMany(database);
			//deleteOne(database);
			//deleteMany(database);
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	private static void deleteMany(MongoDatabase database) {
		MongoCollection<Person> collection = database.getCollection("people", Person.class);
		DeleteResult deleteResult = collection.deleteMany(eq("address.city", "London"));
		System.out.println(deleteResult.getDeletedCount());
	}

	private static void deleteOne(MongoDatabase database) {
		MongoCollection<Person> collection = database.getCollection("people", Person.class);
		collection.deleteOne(eq("address.city", "Wimborne"));
	}

	private static void updateMany(MongoDatabase database) {
		MongoCollection<Person> collection = database.getCollection("people", Person.class);
		UpdateResult updateResult = collection.updateMany(not(eq("address.zip", null)), set("address.zip", null));
		System.out.println(updateResult.getModifiedCount());
	}

	private static void updateOne(MongoDatabase database) {
		MongoCollection<Person> collection = database.getCollection("people", Person.class);
		UpdateResult updateOne = collection.updateOne(eq("name", "Ada Byron"), combine(set("age", 23), set("name", "Ada Lovelace")));
		System.out.println(updateOne.toString());
	}

	private static void findOne(MongoDatabase database) {
		MongoCollection<Person> collection = database.getCollection("people", Person.class);
		Person first = collection.find(eq("address.city", "Wimborne")).first();
		System.out.println(first);
	}

	private static void findAll(MongoDatabase database) {
		MongoCollection<Person> collection = database.getCollection("people", Person.class);
		Block<Person> printBlock = new Block<Person>() {
		    @Override
		    public void apply(final Person person) {
		        System.out.println(person);
		    }
		};
		collection.find().forEach(printBlock);
	}

	private static void insertMany(MongoDatabase database) {
		MongoCollection<Person> collection = database.getCollection("people", Person.class);
		List<Person> people = Arrays.asList(
		        new Person("Charles Babbage", 45, new Address("5 Devonshire Street", "London", "W11")),
		        new Person("Alan Turing", 28, new Address("Bletchley Hall", "Bletchley Park", "MK12")),
		        new Person("Timothy Berners-Lee", 61, new Address("Colehill", "Wimborne", null))
		);
		collection.insertMany(people);
	}

	private static void insertOne(MongoDatabase database) {
		MongoCollection<Person> collection = database.getCollection("people", Person.class);
		Person ada = new Person("Ada Byron", 20, new Address("St James Square", "London", "W1"));
		collection.insertOne(ada);
	}
}
