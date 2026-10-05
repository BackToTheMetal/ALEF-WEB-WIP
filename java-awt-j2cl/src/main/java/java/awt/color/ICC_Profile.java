package java.awt.color;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ICC_Profile implements Serializable {
    public static final int CLASS_INPUT = 0;
    public static final int CLASS_DISPLAY = 1;
    public static final int CLASS_OUTPUT = 2;
    public static final int CLASS_DEVICELINK = 3;
    public static final int CLASS_COLORSPACECONVERSION = 4;
    public static final int CLASS_ABSTRACT = 5;
    public static final int CLASS_NAMEDCOLOR = 6;
    public static final int icSigXYZData = 1482250784;
    public static final int icSigLabData = 1281450528;
    public static final int icSigLuvData = 1282766368;
    public static final int icSigYCbCrData = 1497588338;
    public static final int icSigYxyData = 1501067552;
    public static final int icSigRgbData = 1380401696;
    public static final int icSigGrayData = 1196573017;
    public static final int icSigHsvData = 1213421088;
    public static final int icSigHlsData = 1212961568;
    public static final int icSigCmykData = 1129142603;
    public static final int icSigCmyData = 1129142560;
    public static final int icSigSpace2CLR = 843271250;
    public static final int icSigSpace3CLR = 860048466;
    public static final int icSigSpace4CLR = 876825682;
    public static final int icSigSpace5CLR = 893602898;
    public static final int icSigSpace6CLR = 910380114;
    public static final int icSigSpace7CLR = 927157330;
    public static final int icSigSpace8CLR = 943934546;
    public static final int icSigSpace9CLR = 960711762;
    public static final int icSigSpaceACLR = 1094929490;
    public static final int icSigSpaceBCLR = 1111706706;
    public static final int icSigSpaceCCLR = 1128483922;
    public static final int icSigSpaceDCLR = 1145261138;
    public static final int icSigSpaceECLR = 1162038354;
    public static final int icSigSpaceFCLR = 1178815570;
    public static final int icSigInputClass = 1935896178;
    public static final int icSigDisplayClass = 1835955314;
    public static final int icSigOutputClass = 1886549106;
    public static final int icSigLinkClass = 1818848875;
    public static final int icSigAbstractClass = 1633842036;
    public static final int icSigColorSpaceClass = 1936744803;
    public static final int icSigNamedColorClass = 1852662636;
    public static final int icPerceptual = 0;
    public static final int icRelativeColorimetric = 1;
    public static final int icMediaRelativeColorimetric = 1;
    public static final int icSaturation = 2;
    public static final int icAbsoluteColorimetric = 3;
    public static final int icICCAbsoluteColorimetric = 3;
    public static final int icSigHead = 1751474532;
    public static final int icSigAToB0Tag = 1093812784;
    public static final int icSigAToB1Tag = 1093812785;
    public static final int icSigAToB2Tag = 1093812786;
    public static final int icSigBlueColorantTag = 1649957210;
    public static final int icSigBlueMatrixColumnTag = 1649957210;
    public static final int icSigBlueTRCTag = 1649693251;
    public static final int icSigBToA0Tag = 1110589744;
    public static final int icSigBToA1Tag = 1110589745;
    public static final int icSigBToA2Tag = 1110589746;
    public static final int icSigCalibrationDateTimeTag = 1667329140;
    public static final int icSigCharTargetTag = 1952543335;
    public static final int icSigCopyrightTag = 1668313716;
    public static final int icSigCrdInfoTag = 1668441193;
    public static final int icSigDeviceMfgDescTag = 1684893284;
    public static final int icSigDeviceModelDescTag = 1684890724;
    public static final int icSigDeviceSettingsTag = 1684371059;
    public static final int icSigGamutTag = 1734438260;
    public static final int icSigGrayTRCTag = 1800688195;
    public static final int icSigGreenColorantTag = 1733843290;
    public static final int icSigGreenMatrixColumnTag = 1733843290;
    public static final int icSigGreenTRCTag = 1733579331;
    public static final int icSigLuminanceTag = 1819635049;
    public static final int icSigMeasurementTag = 1835360627;
    public static final int icSigMediaBlackPointTag = 1651208308;
    public static final int icSigMediaWhitePointTag = 2004119668;
    public static final int icSigNamedColor2Tag = 1852009522;
    public static final int icSigOutputResponseTag = 1919251312;
    public static final int icSigPreview0Tag = 1886545200;
    public static final int icSigPreview1Tag = 1886545201;
    public static final int icSigPreview2Tag = 1886545202;
    public static final int icSigProfileDescriptionTag = 1684370275;
    public static final int icSigProfileSequenceDescTag = 1886610801;
    public static final int icSigPs2CRD0Tag = 1886610480;
    public static final int icSigPs2CRD1Tag = 1886610481;
    public static final int icSigPs2CRD2Tag = 1886610482;
    public static final int icSigPs2CRD3Tag = 1886610483;
    public static final int icSigPs2CSATag = 1886597747;
    public static final int icSigPs2RenderingIntentTag = 1886597737;
    public static final int icSigRedColorantTag = 1918392666;
    public static final int icSigRedMatrixColumnTag = 1918392666;
    public static final int icSigRedTRCTag = 1918128707;
    public static final int icSigScreeningDescTag = 1935897188;
    public static final int icSigScreeningTag = 1935897198;
    public static final int icSigTechnologyTag = 1952801640;
    public static final int icSigUcrBgTag = 1650877472;
    public static final int icSigViewingCondDescTag = 1987405156;
    public static final int icSigViewingConditionsTag = 1986618743;
    public static final int icSigChromaticityTag = 1667789421;
    public static final int icSigChromaticAdaptationTag = 1667785060;
    public static final int icSigColorantOrderTag = 1668051567;
    public static final int icSigColorantTableTag = 1668051572;
    public static final int icHdrSize = 0;
    public static final int icHdrCmmId = 4;
    public static final int icHdrVersion = 8;
    public static final int icHdrDeviceClass = 12;
    public static final int icHdrColorSpace = 16;
    public static final int icHdrPcs = 20;
    public static final int icHdrDate = 24;
    public static final int icHdrMagic = 36;
    public static final int icHdrPlatform = 40;
    public static final int icHdrFlags = 44;
    public static final int icHdrManufacturer = 48;
    public static final int icHdrModel = 52;
    public static final int icHdrAttributes = 56;
    public static final int icHdrRenderingIntent = 64;
    public static final int icHdrIlluminant = 68;
    public static final int icHdrCreator = 80;
    public static final int icHdrProfileID = 84;
    public static final int icTagType = 0;
    public static final int icTagReserved = 4;
    public static final int icCurveCount = 8;
    public static final int icCurveData = 12;
    public static final int icXYZNumberX = 8;
    private static final int PROFILE_HEADER_SIZE = 128;
    private static final int PROFILE_MAGIC = 0x61637370;
    private final Map<Integer, byte[]> tags = new LinkedHashMap<>();
    private byte[] data;
    private final int standardColorSpace;

    ICC_Profile(byte[] data, int standardColorSpace) {
        this.data = validatedCopy(data);
        this.standardColorSpace = standardColorSpace;
        readTags();
    }

    public static ICC_Profile getInstance(byte[] data) {
        byte[] profileData = validatedCopy(data);
        int colorType = signatureToColorType(readInt(profileData, icHdrColorSpace));
        if (colorType == ColorSpace.TYPE_RGB) {
            return new ICC_ProfileRGB(profileData, 0);
        }
        if (colorType == ColorSpace.TYPE_GRAY) {
            return new ICC_ProfileGray(profileData, 0);
        }
        return new ICC_Profile(profileData, 0);
    }

    public static ICC_Profile getInstance(int colorSpace) {
        if (colorSpace == ColorSpace.CS_sRGB || colorSpace == ColorSpace.CS_LINEAR_RGB) {
            return new ICC_ProfileRGB(createProfileData(icSigRgbData), colorSpace);
        }
        if (colorSpace == ColorSpace.CS_GRAY) {
            return new ICC_ProfileGray(createProfileData(icSigGrayData), colorSpace);
        }
        if (colorSpace == ColorSpace.CS_CIEXYZ) {
            return new ICC_Profile(createProfileData(icSigXYZData), colorSpace);
        }
        if (colorSpace == ColorSpace.CS_PYCC) {
            return new ICC_Profile(createProfileData(icSigSpace3CLR), colorSpace);
        }
        throw new IllegalArgumentException("Unknown color space: " + colorSpace);
    }

    public static ICC_Profile getInstance(String fileName) throws IOException {
        throw new IOException("Filesystem ICC profiles are unavailable in the browser profile: " + fileName);
    }

    public static ICC_Profile getInstance(InputStream stream) throws IOException {
        if (stream == null) {
            throw new NullPointerException("stream");
        }
        List<Byte> bytes = new ArrayList<>();
        int value;
        while ((value = stream.read()) >= 0) {
            bytes.add((byte) value);
        }
        byte[] data = new byte[bytes.size()];
        for (int index = 0; index < data.length; index++) {
            data[index] = bytes.get(index);
        }
        return getInstance(data);
    }

    public int getMajorVersion() {
        return data[icHdrVersion] & 0xff;
    }

    public int getMinorVersion() {
        return (data[icHdrVersion + 1] >>> 4) & 0x0f;
    }

    public int getProfileClass() {
        return signatureToProfileClass(readInt(data, icHdrDeviceClass));
    }

    public int getColorSpaceType() {
        return signatureToColorType(readInt(data, icHdrColorSpace));
    }

    public int getPCSType() {
        return signatureToColorType(readInt(data, icHdrPcs));
    }

    public void write(String fileName) throws IOException {
        throw new IOException("Filesystem ICC profiles are unavailable in the browser profile: " + fileName);
    }

    public void write(OutputStream stream) throws IOException {
        if (stream == null) {
            throw new NullPointerException("stream");
        }
        stream.write(data);
    }

    public byte[] getData() {
        return copy(data);
    }

    public byte[] getData(int tagSignature) {
        if (tagSignature == icSigHead) {
            byte[] header = new byte[PROFILE_HEADER_SIZE];
            System.arraycopy(data, 0, header, 0, header.length);
            return header;
        }
        byte[] value = tags.get(tagSignature);
        return value == null ? null : copy(value);
    }

    public void setData(int tagSignature, byte[] tagData) {
        if (tagData == null) {
            throw new NullPointerException("tagData");
        }
        if (tagSignature == icSigHead) {
            if (tagData.length < PROFILE_HEADER_SIZE) {
                throw new IllegalArgumentException("ICC header must contain 128 bytes");
            }
            System.arraycopy(tagData, 0, data, 0, PROFILE_HEADER_SIZE);
            validatedCopy(data);
            return;
        }
        tags.put(tagSignature, copy(tagData));
        rebuildProfileData();
    }

    public int getNumComponents() {
        int type = getColorSpaceType();
        if (type == ColorSpace.TYPE_GRAY) {
            return 1;
        }
        if (type >= ColorSpace.TYPE_2CLR && type <= ColorSpace.TYPE_FCLR) {
            return type - ColorSpace.TYPE_2CLR + 2;
        }
        return type == ColorSpace.TYPE_CMYK ? 4 : 3;
    }

    int getStandardColorSpace() {
        return standardColorSpace;
    }

    private void readTags() {
        if (data.length < PROFILE_HEADER_SIZE + 4) {
            return;
        }
        int count = readInt(data, PROFILE_HEADER_SIZE);
        for (int index = 0; index < count; index++) {
            int record = PROFILE_HEADER_SIZE + 4 + index * 12;
            if (record < 0 || record + 12 > data.length) {
                throw new IllegalArgumentException("Invalid ICC tag table");
            }
            int signature = readInt(data, record);
            int offset = readInt(data, record + 4);
            int size = readInt(data, record + 8);
            if (offset < 0 || size < 0 || offset + size < offset || offset + size > data.length) {
                throw new IllegalArgumentException("Invalid ICC tag range");
            }
            byte[] tag = new byte[size];
            System.arraycopy(data, offset, tag, 0, size);
            tags.put(signature, tag);
        }
    }

    private void rebuildProfileData() {
        int tableSize = PROFILE_HEADER_SIZE + 4 + tags.size() * 12;
        int profileSize = tableSize;
        for (byte[] tag : tags.values()) {
            profileSize = aligned(profileSize) + tag.length;
        }
        byte[] result = new byte[profileSize];
        System.arraycopy(data, 0, result, 0, PROFILE_HEADER_SIZE);
        writeInt(result, icHdrSize, profileSize);
        writeInt(result, PROFILE_HEADER_SIZE, tags.size());
        int recordOffset = PROFILE_HEADER_SIZE + 4;
        int dataOffset = tableSize;
        for (Map.Entry<Integer, byte[]> entry : tags.entrySet()) {
            dataOffset = aligned(dataOffset);
            byte[] tag = entry.getValue();
            writeInt(result, recordOffset, entry.getKey());
            writeInt(result, recordOffset + 4, dataOffset);
            writeInt(result, recordOffset + 8, tag.length);
            System.arraycopy(tag, 0, result, dataOffset, tag.length);
            recordOffset += 12;
            dataOffset += tag.length;
        }
        data = result;
    }

    private static int aligned(int value) {
        return value + 3 & ~3;
    }

    private static byte[] validatedCopy(byte[] source) {
        if (source == null) {
            throw new NullPointerException("data");
        }
        if (source.length < PROFILE_HEADER_SIZE || readInt(source, icHdrMagic) != PROFILE_MAGIC) {
            throw new IllegalArgumentException("Invalid ICC profile data");
        }
        int size = readInt(source, icHdrSize);
        if (size < PROFILE_HEADER_SIZE || size > source.length) {
            throw new IllegalArgumentException("Invalid ICC profile size");
        }
        byte[] result = new byte[size];
        System.arraycopy(source, 0, result, 0, size);
        return result;
    }

    private static byte[] createProfileData(int colorSignature) {
        byte[] result = new byte[PROFILE_HEADER_SIZE + 4];
        writeInt(result, icHdrSize, result.length);
        result[icHdrVersion] = 4;
        result[icHdrVersion + 1] = 0x30;
        writeInt(result, icHdrDeviceClass, icSigDisplayClass);
        writeInt(result, icHdrColorSpace, colorSignature);
        writeInt(result, icHdrPcs, icSigXYZData);
        writeInt(result, icHdrMagic, PROFILE_MAGIC);
        return result;
    }

    private static int signatureToProfileClass(int signature) {
        switch (signature) {
            case icSigInputClass: return CLASS_INPUT;
            case icSigDisplayClass: return CLASS_DISPLAY;
            case icSigOutputClass: return CLASS_OUTPUT;
            case icSigLinkClass: return CLASS_DEVICELINK;
            case icSigColorSpaceClass: return CLASS_COLORSPACECONVERSION;
            case icSigAbstractClass: return CLASS_ABSTRACT;
            case icSigNamedColorClass: return CLASS_NAMEDCOLOR;
            default: throw new IllegalArgumentException("Unknown ICC profile class");
        }
    }

    private static int signatureToColorType(int signature) {
        switch (signature) {
            case icSigXYZData: return ColorSpace.TYPE_XYZ;
            case icSigLabData: return ColorSpace.TYPE_Lab;
            case icSigLuvData: return ColorSpace.TYPE_Luv;
            case icSigYCbCrData: return ColorSpace.TYPE_YCbCr;
            case icSigYxyData: return ColorSpace.TYPE_Yxy;
            case icSigRgbData: return ColorSpace.TYPE_RGB;
            case icSigGrayData: return ColorSpace.TYPE_GRAY;
            case icSigHsvData: return ColorSpace.TYPE_HSV;
            case icSigHlsData: return ColorSpace.TYPE_HLS;
            case icSigCmykData: return ColorSpace.TYPE_CMYK;
            case icSigCmyData: return ColorSpace.TYPE_CMY;
            case icSigSpace2CLR: return ColorSpace.TYPE_2CLR;
            case icSigSpace3CLR: return ColorSpace.TYPE_3CLR;
            case icSigSpace4CLR: return ColorSpace.TYPE_4CLR;
            case icSigSpace5CLR: return ColorSpace.TYPE_5CLR;
            case icSigSpace6CLR: return ColorSpace.TYPE_6CLR;
            case icSigSpace7CLR: return ColorSpace.TYPE_7CLR;
            case icSigSpace8CLR: return ColorSpace.TYPE_8CLR;
            case icSigSpace9CLR: return ColorSpace.TYPE_9CLR;
            case icSigSpaceACLR: return ColorSpace.TYPE_ACLR;
            case icSigSpaceBCLR: return ColorSpace.TYPE_BCLR;
            case icSigSpaceCCLR: return ColorSpace.TYPE_CCLR;
            case icSigSpaceDCLR: return ColorSpace.TYPE_DCLR;
            case icSigSpaceECLR: return ColorSpace.TYPE_ECLR;
            case icSigSpaceFCLR: return ColorSpace.TYPE_FCLR;
            default: throw new IllegalArgumentException("Unknown ICC color space signature");
        }
    }

    private static int readInt(byte[] source, int offset) {
        if (offset < 0 || offset + 4 > source.length) {
            throw new IllegalArgumentException("ICC data is truncated");
        }
        return (source[offset] & 0xff) << 24 | (source[offset + 1] & 0xff) << 16 | (source[offset + 2] & 0xff) << 8 | source[offset + 3] & 0xff;
    }

    private static void writeInt(byte[] target, int offset, int value) {
        target[offset] = (byte) (value >>> 24);
        target[offset + 1] = (byte) (value >>> 16);
        target[offset + 2] = (byte) (value >>> 8);
        target[offset + 3] = (byte) value;
    }

    private static byte[] copy(byte[] source) {
        byte[] result = new byte[source.length];
        System.arraycopy(source, 0, result, 0, source.length);
        return result;
    }
    protected void finalize() {}
}
