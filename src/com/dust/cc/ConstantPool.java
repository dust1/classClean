package com.dust.cc;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * 常量池
 */
public class ConstantPool {


    /**
     * 类型索引
     */
    private Map<Integer, List<CPInfo>> typeIndex;

    private CPInfo[] pool;

    private int size;

    public ConstantPool(ClassStream stream) throws IOException {
        size = stream.readUnsignedShort();
        pool = new CPInfo[size];
        typeIndex = new HashMap<>();
        initIndex();


        for (int i = 1; i < size; i++) {
            int tag = stream.readUnsignedByte();
            switch (tag) {
                case CONSTANT_Class:
                    pool[i] = new CONSTANT_Class_info(this, stream);
                    typeIndex.get(CONSTANT_Class).add(pool[i]);
                    break;
                case CONSTANT_Double:
                    pool[i] = new CONSTANT_Double_info(stream);
                    typeIndex.get(CONSTANT_Double).add(pool[i]);
                    i++;
                    break;

                case CONSTANT_Fieldref:
                    pool[i] = new CONSTANT_Fieldref_info(this, stream);
                    typeIndex.get(CONSTANT_Fieldref).add(pool[i]);
                    break;

                case CONSTANT_Float:
                    pool[i] = new CONSTANT_Float_info(stream);
                    typeIndex.get(CONSTANT_Float).add(pool[i]);
                    break;

                case CONSTANT_Integer:
                    pool[i] = new CONSTANT_Integer_info(stream);
                    typeIndex.get(CONSTANT_Integer).add(pool[i]);
                    break;

                case CONSTANT_InterfaceMethodref:
                    pool[i] = new CONSTANT_InterfaceMethodref_info(this, stream);
                    typeIndex.get(CONSTANT_InterfaceMethodref).add(pool[i]);
                    break;

                case CONSTANT_InvokeDynamic:
                    pool[i] = new CONSTANT_InvokeDynamic_info(this, stream);
                    typeIndex.get(CONSTANT_InvokeDynamic).add(pool[i]);
                    break;

                case CONSTANT_Dynamic:
                    pool[i] = new CONSTANT_Dynamic_info(this, stream);
                    typeIndex.get(CONSTANT_Dynamic).add(pool[i]);
                    break;

                case CONSTANT_Long:
                    pool[i] = new CONSTANT_Long_info(stream);
                    typeIndex.get(CONSTANT_Long).add(pool[i]);
                    i++;
                    break;

                case CONSTANT_MethodHandle:
                    pool[i] = new CONSTANT_MethodHandle_info(this, stream);
                    typeIndex.get(CONSTANT_MethodHandle).add(pool[i]);
                    break;

                case CONSTANT_MethodType:
                    pool[i] = new CONSTANT_MethodType_info(this, stream);
                    typeIndex.get(CONSTANT_MethodType).add(pool[i]);
                    break;

                case CONSTANT_Methodref:
                    pool[i] = new CONSTANT_Methodref_info(this, stream);
                    typeIndex.get(CONSTANT_Methodref).add(pool[i]);
                    break;

                case CONSTANT_Module:
                    pool[i] = new CONSTANT_Module_info(this, stream);
                    typeIndex.get(CONSTANT_Module).add(pool[i]);
                    break;

                case CONSTANT_NameAndType:
                    pool[i] = new CONSTANT_NameAndType_info(this, stream);
                    typeIndex.get(CONSTANT_NameAndType).add(pool[i]);
                    break;

                case CONSTANT_Package:
                    pool[i] = new CONSTANT_Package_info(this, stream);
                    typeIndex.get(CONSTANT_Package).add(pool[i]);
                    break;

                case CONSTANT_String:
                    pool[i] = new CONSTANT_String_info(this, stream);
                    typeIndex.get(CONSTANT_String).add(pool[i]);
                    break;

                case CONSTANT_Utf8:
                    pool[i] = new CONSTANT_Utf8_info(stream);
                    typeIndex.get(CONSTANT_Utf8).add(pool[i]);
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    private void initIndex() {
        for (int i = 1; i <= 20; i++) {
            typeIndex.put(i, new ArrayList<>());
        }
    }

    public static final int CONSTANT_Utf8 = 1;
    public static final int CONSTANT_Integer = 3;
    public static final int CONSTANT_Float = 4;
    public static final int CONSTANT_Long = 5;
    public static final int CONSTANT_Double = 6;
    public static final int CONSTANT_Class = 7;
    public static final int CONSTANT_String = 8;
    public static final int CONSTANT_Fieldref = 9;
    public static final int CONSTANT_Methodref = 10;
    public static final int CONSTANT_InterfaceMethodref = 11;
    public static final int CONSTANT_NameAndType = 12;
    public static final int CONSTANT_MethodHandle = 15;
    public static final int CONSTANT_MethodType = 16;
    public static final int CONSTANT_Dynamic = 17;
    public static final int CONSTANT_InvokeDynamic = 18;
    public static final int CONSTANT_Module = 19;
    public static final int CONSTANT_Package = 20;

    public int size() {
        return size;
    }

    public List<CPInfo> getCPList(int tag) {
        return typeIndex.get(tag);
    }

    public int byteLength() {
        int length = 2;
        for (int i = 1; i < size(); ) {
            CPInfo cpInfo = pool[i];
            length += cpInfo.byteLength();
            i += cpInfo.size();
        }
        return length;
    }

    public CPInfo get(int index) {
        if (index <= 0 || index >= pool.length)
            throw new IllegalArgumentException();
        CPInfo info = pool[index];
        if (info == null) {
            // this occurs for indices referencing the "second half" of an
            // 8 byte constant, such as CONSTANT_Double or CONSTANT_Long
            throw new IllegalArgumentException();
        }
        return pool[index];
    }

    private CPInfo get(int index, int expected_type){
        CPInfo info = get(index);
        if (info.getTag() != expected_type)
           throw new IllegalArgumentException();
        return info;
    }

    public CONSTANT_Utf8_info getUTF8Info(int index){
        return ((CONSTANT_Utf8_info) get(index, CONSTANT_Utf8));
    }

    public CONSTANT_Class_info getClassInfo(int index){
        return ((CONSTANT_Class_info) get(index, CONSTANT_Class));
    }

    public CONSTANT_Module_info getModuleInfo(int index) {
        return ((CONSTANT_Module_info) get(index, CONSTANT_Module));
    }

    public CONSTANT_NameAndType_info getNameAndTypeInfo(int index) {
        return ((CONSTANT_NameAndType_info) get(index, CONSTANT_NameAndType));
    }

    public CONSTANT_Package_info getPackageInfo(int index){
        return ((CONSTANT_Package_info) get(index, CONSTANT_Package));
    }

    public String getUTF8Value(int index){
        return getUTF8Info(index).value;
    }

    public int getUTF8Index(String value) throws Exception {
        for (int i = 1; i < pool.length; i++) {
            CPInfo info = pool[i];
            if (info instanceof CONSTANT_Utf8_info &&
                    ((CONSTANT_Utf8_info) info).value.equals(value))
                return i;
        }
        throw new Exception();
    }

    public Iterable<CPInfo> entries() {
        return () -> new Iterator<CPInfo>() {

            public boolean hasNext() {
                return next < pool.length;
            }

            public CPInfo next() {
                current = pool[next];
                switch (current.getTag()) {
                    case CONSTANT_Double:
                    case CONSTANT_Long:
                        next += 2;
                        break;
                    default:
                        next += 1;
                }
                return current;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }

            private CPInfo current;
            private int next = 1;

        };
    }

    public enum RefKind {
        REF_getField(1),
        REF_getStatic(2),
        REF_putField(3),
        REF_putStatic(4),
        REF_invokeVirtual(5),
        REF_invokeStatic(6),
        REF_invokeSpecial(7),
        REF_newInvokeSpecial(8),
        REF_invokeInterface(9);

        public final int tag;

        RefKind(int tag) {
            this.tag = tag;
        }

        static RefKind getRefkind(int tag) {
            switch(tag) {
                case 1:
                    return REF_getField;
                case 2:
                    return REF_getStatic;
                case 3:
                    return REF_putField;
                case 4:
                    return REF_putStatic;
                case 5:
                    return REF_invokeVirtual;
                case 6:
                    return REF_invokeStatic;
                case 7:
                    return REF_invokeSpecial;
                case 8:
                    return REF_newInvokeSpecial;
                case 9:
                    return REF_invokeInterface;
                default:
                    return null;
            }
        }
    }

    /**
     * 常量池的元素信息
     */
    static abstract class CPInfo {
        CPInfo() {
            this.cp = null;
        }

        CPInfo(ConstantPool cp) {
            this.cp = cp;
        }

        public abstract int getTag();

        /** The number of slots in the constant pool used by this entry.
         * 2 for CONSTANT_Double and CONSTANT_Long; 1 for everything else. */
        public int size() {
            return 1;
        }

        public abstract int byteLength();

        public abstract <R,D> R accept(Visitor<R,D> visitor, D data);

        protected final ConstantPool cp;
    }

    public static abstract class CPRefInfo extends CPInfo {
        protected CPRefInfo(ConstantPool cp, ClassStream cr, int tag) throws IOException {
            super(cp);
            this.tag = tag;
            class_index = cr.readUnsignedShort();
            name_and_type_index = cr.readUnsignedShort();
        }

        protected CPRefInfo(ConstantPool cp, int tag, int class_index, int name_and_type_index) {
            super(cp);
            this.tag = tag;
            this.class_index = class_index;
            this.name_and_type_index = name_and_type_index;
        }

        public int getTag() {
            return tag;
        }

        public int byteLength() {
            return 5;
        }

        public CONSTANT_Class_info getClassInfo() {
            return cp.getClassInfo(class_index);
        }

        public String getClassName() {
            return cp.getClassInfo(class_index).getName();
        }

        public CONSTANT_NameAndType_info getNameAndTypeInfo() {
            return cp.getNameAndTypeInfo(name_and_type_index);
        }

        public final int tag;
        public final int class_index;
        public final int name_and_type_index;
    }


    public interface Visitor<R,P> {
        R visitClass(CONSTANT_Class_info info, P p);
        R visitDouble(CONSTANT_Double_info info, P p);
        R visitFieldref(CONSTANT_Fieldref_info info, P p);
        R visitFloat(CONSTANT_Float_info info, P p);
        R visitInteger(CONSTANT_Integer_info info, P p);
        R visitInterfaceMethodref(CONSTANT_InterfaceMethodref_info info, P p);
        R visitInvokeDynamic(CONSTANT_InvokeDynamic_info info, P p);
        R visitDynamicConstant(CONSTANT_Dynamic_info info, P p);
        R visitLong(CONSTANT_Long_info info, P p);
        R visitMethodref(CONSTANT_Methodref_info info, P p);
        R visitMethodHandle(CONSTANT_MethodHandle_info info, P p);
        R visitMethodType(CONSTANT_MethodType_info info, P p);
        R visitModule(CONSTANT_Module_info info, P p);
        R visitNameAndType(CONSTANT_NameAndType_info info, P p);
        R visitPackage(CONSTANT_Package_info info, P p);
        R visitString(CONSTANT_String_info info, P p);
        R visitUtf8(CONSTANT_Utf8_info info, P p);
    }

    public static class CONSTANT_Class_info extends CPInfo {
        CONSTANT_Class_info(ConstantPool cp, ClassStream cr) throws IOException {
            super(cp);
            name_index = cr.readUnsignedShort();
        }

        public CONSTANT_Class_info(ConstantPool cp, int name_index) {
            super(cp);
            this.name_index = name_index;
        }

        public int getTag() {
            return CONSTANT_Class;
        }

        public int  byteLength() {
            return 3;
        }

        /**
         * Get the raw value of the class referenced by this constant pool entry.
         * This will either be the name of the class, in internal form, or a
         * descriptor for an array class.
         * @return the raw value of the class
         */
        public String getName() {
            return cp.getUTF8Value(name_index);
        }

        /**
         * If this constant pool entry identifies either a class or interface type,
         * or a possibly multi-dimensional array of a class of interface type,
         * return the name of the class or interface in internal form. Otherwise,
         * (i.e. if this is a possibly multi-dimensional array of a primitive type),
         * return null.
         * @return the base class or interface name
         */
        public String getBaseName() {
            String name = getName();
            if (name.startsWith("[")) {
                int index = name.indexOf("[L");
                if (index == -1)
                    return null;
                return name.substring(index + 2, name.length() - 1);
            } else
                return name;
        }

        public int getDimensionCount() {
            String name = getName();
            int count = 0;
            while (name.charAt(count) == '[')
                count++;
            return count;
        }

        @Override
        public String toString() {
            return "CONSTANT_Class_info[name_index: " + name_index + "]";
        }

        public <R, D> R accept(Visitor<R, D> visitor, D data) {
            return visitor.visitClass(this, data);
        }

        public final int name_index;
    }

    public static class CONSTANT_Double_info extends CPInfo {
        CONSTANT_Double_info(ClassStream cr) throws IOException {
            value = cr.readDouble();
        }

        public CONSTANT_Double_info(double value) {
            this.value = value;
        }

        public int getTag() {
            return CONSTANT_Double;
        }

        public int  byteLength() {
            return 9;
        }

        @Override
        public int size() {
            return 2;
        }

        @Override
        public String toString() {
            return "CONSTANT_Double_info[value: " + value + "]";
        }

        public <R, D> R accept(Visitor<R, D> visitor, D data) {
            return visitor.visitDouble(this, data);
        }

        public final double value;
    }

    public static class CONSTANT_Fieldref_info extends CPRefInfo {
        CONSTANT_Fieldref_info(ConstantPool cp, ClassStream cr) throws IOException {
            super(cp, cr, CONSTANT_Fieldref);
        }

        public CONSTANT_Fieldref_info(ConstantPool cp, int class_index, int name_and_type_index) {
            super(cp, CONSTANT_Fieldref, class_index, name_and_type_index);
        }

        @Override
        public String toString() {
            return "CONSTANT_Fieldref_info[class_index: " + class_index + ", name_and_type_index: " + name_and_type_index + "]";
        }

        public <R, D> R accept(Visitor<R, D> visitor, D data) {
            return visitor.visitFieldref(this, data);
        }
    }

    public static class CONSTANT_Float_info extends CPInfo {
        CONSTANT_Float_info(ClassStream cr) throws IOException {
            value = cr.readFloat();
        }

        public CONSTANT_Float_info(float value) {
            this.value = value;
        }

        public int getTag() {
            return CONSTANT_Float;
        }

        public int byteLength() {
            return 5;
        }

        @Override
        public String toString() {
            return "CONSTANT_Float_info[value: " + value + "]";
        }

        public <R, D> R accept(Visitor<R, D> visitor, D data) {
            return visitor.visitFloat(this, data);
        }

        public final float value;
    }

    public static class CONSTANT_Integer_info extends CPInfo {
        CONSTANT_Integer_info(ClassStream cr) throws IOException {
            value = cr.readInt();
        }

        public CONSTANT_Integer_info(int value) {
            this.value = value;
        }

        public int getTag() {
            return CONSTANT_Integer;
        }

        public int byteLength() {
            return 5;
        }

        @Override
        public String toString() {
            return "CONSTANT_Integer_info[value: " + value + "]";
        }

        public <R, D> R accept(Visitor<R, D> visitor, D data) {
            return visitor.visitInteger(this, data);
        }

        public final int value;
    }

    public static class CONSTANT_InterfaceMethodref_info extends CPRefInfo {
        CONSTANT_InterfaceMethodref_info(ConstantPool cp, ClassStream cr) throws IOException {
            super(cp, cr, CONSTANT_InterfaceMethodref);
        }

        public CONSTANT_InterfaceMethodref_info(ConstantPool cp, int class_index, int name_and_type_index) {
            super(cp, CONSTANT_InterfaceMethodref, class_index, name_and_type_index);
        }

        @Override
        public String toString() {
            return "CONSTANT_InterfaceMethodref_info[class_index: " + class_index + ", name_and_type_index: " + name_and_type_index + "]";
        }

        public <R, D> R accept(Visitor<R, D> visitor, D data) {
            return visitor.visitInterfaceMethodref(this, data);
        }
    }

    public static class CONSTANT_InvokeDynamic_info extends CPInfo {
        CONSTANT_InvokeDynamic_info(ConstantPool cp, ClassStream cr) throws IOException {
            super(cp);
            bootstrap_method_attr_index = cr.readUnsignedShort();
            name_and_type_index = cr.readUnsignedShort();
        }

        public CONSTANT_InvokeDynamic_info(ConstantPool cp, int bootstrap_method_index, int name_and_type_index) {
            super(cp);
            this.bootstrap_method_attr_index = bootstrap_method_index;
            this.name_and_type_index = name_and_type_index;
        }

        public int getTag() {
            return CONSTANT_InvokeDynamic;
        }

        public int byteLength() {
            return 5;
        }

        @Override
        public String toString() {
            return "CONSTANT_InvokeDynamic_info[bootstrap_method_index: " + bootstrap_method_attr_index + ", name_and_type_index: " + name_and_type_index + "]";
        }

        public <R, D> R accept(Visitor<R, D> visitor, D data) {
            return visitor.visitInvokeDynamic(this, data);
        }

        public CONSTANT_NameAndType_info getNameAndTypeInfo() {
            return cp.getNameAndTypeInfo(name_and_type_index);
        }

        public final int bootstrap_method_attr_index;
        public final int name_and_type_index;
    }

    public static class CONSTANT_Long_info extends CPInfo {
        CONSTANT_Long_info(ClassStream cr) throws IOException {
            value = cr.readLong();
        }

        public CONSTANT_Long_info(long value) {
            this.value = value;
        }

        public int getTag() {
            return CONSTANT_Long;
        }

        @Override
        public int size() {
            return 2;
        }

        public int byteLength() {
            return 9;
        }

        @Override
        public String toString() {
            return "CONSTANT_Long_info[value: " + value + "]";
        }

        public <R, D> R accept(Visitor<R, D> visitor, D data) {
            return visitor.visitLong(this, data);
        }

        public final long value;
    }

    public static class CONSTANT_MethodHandle_info extends CPInfo {
        CONSTANT_MethodHandle_info(ConstantPool cp, ClassStream cr) throws IOException {
            super(cp);
            reference_kind =  RefKind.getRefkind(cr.readUnsignedByte());
            reference_index = cr.readUnsignedShort();
        }

        public CONSTANT_MethodHandle_info(ConstantPool cp, RefKind ref_kind, int member_index) {
            super(cp);
            this.reference_kind = ref_kind;
            this.reference_index = member_index;
        }

        public int getTag() {
            return CONSTANT_MethodHandle;
        }

        public int byteLength() {
            return 4;
        }

        @Override
        public String toString() {
            return "CONSTANT_MethodHandle_info[ref_kind: " + reference_kind + ", member_index: " + reference_index + "]";
        }

        public <R, D> R accept(Visitor<R, D> visitor, D data) {
            return visitor.visitMethodHandle(this, data);
        }

        public CPRefInfo getCPRefInfo() {
            int expected = CONSTANT_Methodref;
            int actual = cp.get(reference_index).getTag();
            // allow these tag types also:
            switch (actual) {
                case CONSTANT_Fieldref:
                case CONSTANT_InterfaceMethodref:
                    expected = actual;
            }
            return (CPRefInfo) cp.get(reference_index, expected);
        }

        public final RefKind reference_kind;
        public final int reference_index;
    }

    public static class CONSTANT_MethodType_info extends CPInfo {
        CONSTANT_MethodType_info(ConstantPool cp, ClassStream cr) throws IOException {
            super(cp);
            descriptor_index = cr.readUnsignedShort();
        }

        public CONSTANT_MethodType_info(ConstantPool cp, int signature_index) {
            super(cp);
            this.descriptor_index = signature_index;
        }

        public int getTag() {
            return CONSTANT_MethodType;
        }

        public int byteLength() {
            return 3;
        }

        @Override
        public String toString() {
            return "CONSTANT_MethodType_info[signature_index: " + descriptor_index + "]";
        }

        public <R, D> R accept(Visitor<R, D> visitor, D data) {
            return visitor.visitMethodType(this, data);
        }

        public String getType() {
            return cp.getUTF8Value(descriptor_index);
        }

        public final int descriptor_index;
    }

    public static class CONSTANT_Methodref_info extends CPRefInfo {
        CONSTANT_Methodref_info(ConstantPool cp, ClassStream cr) throws IOException {
            super(cp, cr, CONSTANT_Methodref);
        }

        public CONSTANT_Methodref_info(ConstantPool cp, int class_index, int name_and_type_index) {
            super(cp, CONSTANT_Methodref, class_index, name_and_type_index);
        }

        @Override
        public String toString() {
            return "CONSTANT_Methodref_info[class_index: " + class_index + ", name_and_type_index: " + name_and_type_index + "]";
        }

        public <R, D> R accept(Visitor<R, D> visitor, D data) {
            return visitor.visitMethodref(this, data);
        }
    }

    public static class CONSTANT_Module_info extends CPInfo {
        CONSTANT_Module_info(ConstantPool cp, ClassStream cr) throws IOException {
            super(cp);
            name_index = cr.readUnsignedShort();
        }

        public CONSTANT_Module_info(ConstantPool cp, int name_index) {
            super(cp);
            this.name_index = name_index;
        }

        public int getTag() {
            return CONSTANT_Module;
        }

        public int  byteLength() {
            return 3;
        }

        /**
         * Get the raw value of the module name referenced by this constant pool entry.
         * This will be the name of the module.
         * @return the raw value of the module name
         */
        public String getName() {
            return cp.getUTF8Value(name_index);
        }

        @Override
        public String toString() {
            return "CONSTANT_Module_info[name_index: " + name_index + "]";
        }

        public <R, D> R accept(Visitor<R, D> visitor, D data) {
            return visitor.visitModule(this, data);
        }

        public final int name_index;
    }

    public static class CONSTANT_NameAndType_info extends CPInfo {
        CONSTANT_NameAndType_info(ConstantPool cp, ClassStream cr) throws IOException {
            super(cp);
            name_index = cr.readUnsignedShort();
            type_index = cr.readUnsignedShort();
        }

        public CONSTANT_NameAndType_info(ConstantPool cp, int name_index, int type_index) {
            super(cp);
            this.name_index = name_index;
            this.type_index = type_index;
        }

        public int getTag() {
            return CONSTANT_NameAndType;
        }

        public int byteLength() {
            return 5;
        }

        public String getName() {
            return cp.getUTF8Value(name_index);
        }

        public String getType() {
            return cp.getUTF8Value(type_index);
        }

        public <R, D> R accept(Visitor<R, D> visitor, D data) {
            return visitor.visitNameAndType(this, data);
        }

        @Override
        public String toString() {
            return "CONSTANT_NameAndType_info[name_index: " + name_index + ", type_index: " + type_index + "]";
        }

        public final int name_index;
        public final int type_index;
    }

    public static class CONSTANT_Dynamic_info extends CPInfo {
        CONSTANT_Dynamic_info(ConstantPool cp, ClassStream cr) throws IOException {
            super(cp);
            bootstrap_method_attr_index = cr.readUnsignedShort();
            name_and_type_index = cr.readUnsignedShort();
        }

        public CONSTANT_Dynamic_info(ConstantPool cp, int bootstrap_method_index, int name_and_type_index) {
            super(cp);
            this.bootstrap_method_attr_index = bootstrap_method_index;
            this.name_and_type_index = name_and_type_index;
        }

        public int getTag() {
            return CONSTANT_Dynamic;
        }

        public int byteLength() {
            return 5;
        }

        @Override
        public String toString() {
            return "CONSTANT_Dynamic_info[bootstrap_method_index: " + bootstrap_method_attr_index + ", name_and_type_index: " + name_and_type_index + "]";
        }

        public <R, D> R accept(Visitor<R, D> visitor, D data) {
            return visitor.visitDynamicConstant(this, data);
        }

        public CONSTANT_NameAndType_info getNameAndTypeInfo() {
            return cp.getNameAndTypeInfo(name_and_type_index);
        }

        public final int bootstrap_method_attr_index;
        public final int name_and_type_index;
    }

    public static class CONSTANT_Package_info extends CPInfo {
        CONSTANT_Package_info(ConstantPool cp, ClassStream cr) throws IOException {
            super(cp);
            name_index = cr.readUnsignedShort();
        }

        public CONSTANT_Package_info(ConstantPool cp, int name_index) {
            super(cp);
            this.name_index = name_index;
        }

        public int getTag() {
            return CONSTANT_Package;
        }

        public int  byteLength() {
            return 3;
        }

        /**
         * Get the raw value of the package name referenced by this constant pool entry.
         * This will be the name of the package, in internal form.
         * @return the raw value of the module name
         */
        public String getName() {
            return cp.getUTF8Value(name_index);
        }

        @Override
        public String toString() {
            return "CONSTANT_Package_info[name_index: " + name_index + "]";
        }

        public <R, D> R accept(Visitor<R, D> visitor, D data) {
            return visitor.visitPackage(this, data);
        }

        public final int name_index;
    }

    public static class CONSTANT_String_info extends CPInfo {
        CONSTANT_String_info(ConstantPool cp, ClassStream cr) throws IOException {
            super(cp);
            string_index = cr.readUnsignedShort();
        }

        public CONSTANT_String_info(ConstantPool cp, int string_index) {
            super(cp);
            this.string_index = string_index;
        }

        public int getTag() {
            return CONSTANT_String;
        }

        public int byteLength() {
            return 3;
        }

        public String getString() {
            return cp.getUTF8Value(string_index);
        }

        public <R, D> R accept(Visitor<R, D> visitor, D data) {
            return visitor.visitString(this, data);
        }

        @Override
        public String toString() {
            return "CONSTANT_String_info[class_index: " + string_index + "]";
        }

        public final int string_index;
    }

    public static class CONSTANT_Utf8_info extends CPInfo {
        CONSTANT_Utf8_info(ClassStream cr) throws IOException {
            value = cr.readUTF();
        }

        public CONSTANT_Utf8_info(String value) {
            this.value = value;
        }

        public int getTag() {
            return CONSTANT_Utf8;
        }

        public int byteLength() {
            class SizeOutputStream extends OutputStream {
                @Override
                public void write(int b) {
                    size++;
                }
                int size;
            }
            SizeOutputStream sizeOut = new SizeOutputStream();
            DataOutputStream out = new DataOutputStream(sizeOut);
            try { out.writeUTF(value); } catch (IOException ignore) { }
            return 1 + sizeOut.size;
        }

        @Override
        public String toString() {
            if (value.length() < 32 && isPrintableAscii(value))
                return "CONSTANT_Utf8_info[value: \"" + value + "\"]";
            else
                return "CONSTANT_Utf8_info[value: (" + value.length() + " chars)]";
        }

        static boolean isPrintableAscii(String s) {
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c < 32 || c >= 127)
                    return false;
            }
            return true;
        }

        public <R, D> R accept(Visitor<R, D> visitor, D data) {
            return visitor.visitUtf8(this, data);
        }

        public final String value;
    }

}
