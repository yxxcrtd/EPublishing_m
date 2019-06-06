package cn.digitalpublishing.redis.po;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Book implements Serializable {

	private String id;

	private String title;

	// 书的访问量 或 购买次数
	private long count;

	private String author;

	private String date;

	private String publisher;

	private int type;

	public Book() {

	}

	public Book(String id, String title, long count) {
		super();
		this.id = id;
		this.title = title;
		this.count = count;
	}

	public Book(String id, String title, String author, String date, String publisher, int type) {
		super();
		this.id = id;
		this.title = title;
		this.author = author;
		this.date = date;
		this.publisher = publisher;
		this.type = type;
	}

	public Book(String id, String title, String author) {
		super();
		this.id = id;
		this.title = title;
		this.author = author;
	}

	public Book(String count, String id, String title, String author, String date, String publisher) {
		super();
		this.id = id;
		this.title = title;
		this.author = author;
		this.date = date;
		this.publisher = publisher;
	}

	@Override
	public String toString() {
		return id + "_|_" + title + "_|_" + count;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
