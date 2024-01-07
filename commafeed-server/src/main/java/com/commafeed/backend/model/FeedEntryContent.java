package com.commafeed.backend.model;

import java.sql.Types;
import java.util.Set;

import org.hibernate.annotations.JdbcTypeCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "FEEDENTRYCONTENTS")
@SuppressWarnings("serial")
@Getter
@Setter
public class FeedEntryContent extends AbstractModel {

	@Column(length = 2048)
	private String title;

	@Column(length = 40)
	private String titleHash;

	@Lob
	@Column(length = Integer.MAX_VALUE)
	@JdbcTypeCode(Types.LONGVARCHAR)
	private String content;

	@Column(length = 40)
	private String contentHash;

	@Column(name = "author", length = 128)
	private String author;

	@Column(length = 2048)
	private String enclosureUrl;

	@Column(length = 255)
	private String enclosureType;

	@Lob
	@Column(length = Integer.MAX_VALUE)
	@JdbcTypeCode(Types.LONGVARCHAR)
	private String mediaDescription;

	@Column(length = 2048)
	private String mediaThumbnailUrl;

	private Integer mediaThumbnailWidth;
	private Integer mediaThumbnailHeight;

	@Column(length = 4096)
	private String categories;

	@OneToMany(mappedBy = "content")
	private Set<FeedEntry> entries;

}
