package com.dust.cc;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 封装Class文件的Stream
 */
public class ClassStream {

    private DataInputStream inputStream;

    public static ClassStream create(Path path) {
        return new ClassStream(path);
    }

    public static ClassStream create(File file) {
        return new ClassStream(file);
    }

    private ClassStream(File file) {
        this(file.toPath());
    }

    private ClassStream(Path path) {
        try {
            this.inputStream = new DataInputStream(Files.newInputStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int readUnsignedByte() throws IOException {
        return inputStream.readUnsignedByte();
    }

    public int readUnsignedShort() throws IOException {
        return inputStream.readUnsignedShort();
    }

    public int readInt() throws IOException {
        return inputStream.readInt();
    }

    public long readLong() throws IOException {
        return inputStream.readLong();
    }

    public float readFloat() throws IOException {
        return inputStream.readFloat();
    }

    public double readDouble() throws IOException {
        return inputStream.readDouble();
    }

    public String readUTF() throws IOException {
        return inputStream.readUTF();
    }

    public void close() throws IOException {
        inputStream.close();
    }

}
