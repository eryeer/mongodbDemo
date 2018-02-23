package cn.mlb.mongo.demo;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.*;

public class MongoDocument {
	public static void main(String[] args) {
		try {
			// 连接到MongoDB服务 如果是远程连接可以替换“localhost”为服务器所在IP地址
			// ServerAddress()两个参数分别为 服务器地址 和 端口
			ServerAddress serverAddress = new ServerAddress("localhost", 27017);
			List<ServerAddress> addrs = new ArrayList<ServerAddress>();
			addrs.add(serverAddress);

			// MongoCredential.createScramSha1Credential()三个参数分别为 用户名 数据库名称 密码
			MongoCredential credential = MongoCredential
					.createScramSha1Credential("user", "shop",
							"pwd".toCharArray());
			List<MongoCredential> credentials = new ArrayList<MongoCredential>();
			credentials.add(credential);

			// 通过连接认证获取MongoDB连接
			MongoClient mongoClient = new MongoClient(addrs, credentials);

			// 连接到数据库
			MongoDatabase mongoDatabase = mongoClient.getDatabase("shop");
			System.out.println("Connect to database successfully");
			// mongoDatabase.createCollection("cat");
			// System.out.println("create cat");
			MongoCollection<Document> collection = mongoDatabase
					.getCollection("stu");
			// find one in filter
			Document first = collection.find(eq("sn", 100)).first();
			System.out.println(first.toJson());

			// find all in filter
			// FindIterable<Document> find = collection.find(Filters.lte("sn",
			// 100));
			// MongoCursor<Document> iterator = find.iterator();
			// while (iterator.hasNext()) {
			// System.out.println(iterator.next().toJson());
			//
			// }

			Block<Document> printBlock = new Block<Document>() {
				@Override
				public void apply(final Document document) {
					System.out.println(document.toJson());
				}
			};
			collection.find(lte("sn", 100)).forEach(printBlock);

			/*
			 * FindIterable<Document> find = collection.find();
			 * MongoCursor<Document> cursor = find.iterator(); while
			 * (cursor.hasNext()) { Document next = cursor.next(); String json =
			 * next.toJson(); System.out.println(json); //
			 * System.out.println(next.toString());
			 * 
			 * }
			 */
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}
}
