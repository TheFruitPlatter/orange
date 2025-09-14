package com.langwuyue.orange.zookeeper;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


public class OrangeStringSerializer {

	private final Charset charset;

	/**
	 * {@link OrangeStringSerializer} to use 7 bit ASCII, a.k.a. ISO646-US, a.k.a. the Basic Latin block of the Unicode
	 * character set.
	 *
	 * @see StandardCharsets#US_ASCII
	 * @since 2.1
	 */
	public static final OrangeStringSerializer US_ASCII = new OrangeStringSerializer(StandardCharsets.US_ASCII);

	/**
	 * {@link OrangeStringSerializer} to use ISO Latin Alphabet No. 1, a.k.a. ISO-LATIN-1.
	 *
	 * @see StandardCharsets#ISO_8859_1
	 * @since 2.1
	 */
	public static final OrangeStringSerializer ISO_8859_1 = new OrangeStringSerializer(StandardCharsets.ISO_8859_1);

	/**
	 * {@link OrangeStringSerializer} to use 8 bit UCS Transformation Format.
	 *
	 * @see StandardCharsets#UTF_8
	 * @since 2.1
	 */
	public static final OrangeStringSerializer UTF_8 = new OrangeStringSerializer(StandardCharsets.UTF_8);

	/**
	 * Creates a new {@link OrangeStringSerializer} using {@link StandardCharsets#UTF_8 UTF-8}.
	 */
	public OrangeStringSerializer() {
		this(StandardCharsets.UTF_8);
	}

	/**
	 * Creates a new {@link OrangeStringSerializer} using the given {@link Charset} to encode and decode strings.
	 *
	 * @param charset must not be {@literal null}.
	 */
	public OrangeStringSerializer(Charset charset) {
		this.charset = charset;
	}

	public String deserialize(byte[] bytes) {
		return (bytes == null ? null : new String(bytes, charset));
	}

	public byte[] serialize(String string) {
		return (string == null ? null : string.getBytes(charset));
	}
}
