package cn.cjwddz.knowu.service;

/**
 * Created by Administrator on 2018/7/20.
 */

public class Protocol {
    private final static byte start = 0x0a,end=0x0b;
    public static int lastIntensity = 0X0;
    /*控制位类型*/
    public enum Control{
        READ((byte)0x01),WRITE((byte)0x02),READ_RESPONSE((byte)0x03),WRITE_RESPONSE((byte)0x04);
        byte value;
        Control(byte value){
            this.value=value;
        }
    };
    /*类型*/
    public enum Type{
        SWITCH((byte) 0xc0),MODE((byte) 0xc1),INTENSITY((byte) 0xc2),CHECK((byte) 0xc3),LINK((byte) 0xc4),ELECTRIC((byte)0xc5);
        byte value;
        Type(byte value){
            this.value=value;
        }
        byte getIntensity(int intensity){
            if (intensity<=0)
                return 0x01;

            return (byte)(0x00+intensity);
        }
        byte getCheck(int value){
            if (value<=0)
                return 0x01;
            if(value>5)
                return 0x05;
            return (byte)(0x00+value);
        }
    }
    /*数据*/
    public enum Data{
        ENABLE((byte)0x01),DISABLE((byte)0x00),
        MODE((byte)0x0),
        NO_CONNECT((byte)0x01),WAIT_CONNECT((byte)0x02),CONNECTED((byte)0x03),
        SUCCESS((byte)0x01),FAILED((byte)0x00);
        byte value;
        Data(byte value){
            this.value=value;
        }
        public byte getMode(int mode){
            byte value;
            switch (mode){
                case 0:
                    value = 0x01;
                    break;
                case 1:
                    value = 0x02;
                    break;
                case 2:
                    value = 0x03;
                    break;
                case 3:
                    value = 0x04;
                    break;
                default:
                    value = 0x01;
                    break;
            }
            return value;
        }
    }
    // 打开开关命令
    public static byte[] getOpenInstruct(){
        byte[] data =new byte[]{start,0x00,0x00,Control.WRITE.value,Type.SWITCH.value,Data.ENABLE.value,end};
        data[1] = (byte)(0x00+data.length);
        data[6] = (byte) (data[0]+data[1]+data[2]+data[3]+data[4]+data[5]);
        return data;
    }
    // 关闭命令
    public static byte[] getCloseInstruct(){
        byte[] data =new byte[]{start,0x00,0x00,Control.WRITE.value,Type.SWITCH.value,Data.DISABLE.value,end};
        data[1] = (byte)(0x00+data.length);
        data[6] = (byte) (data[0]+data[1]+data[2]+data[3]+data[4]+data[5]);
        return data;
    }
    // 设置强度指令
    public static byte[] getIntensitySetInstruct(int intensity){
        lastIntensity = intensity;
        byte[] data =new byte[]{start,0x00,(byte) (0x00+intensity),Control.WRITE.value,Type.INTENSITY.value,Type.INTENSITY.getIntensity(intensity),end};
        data[1] = (byte)(0x00+data.length);
        data[6] = (byte) (data[0]+data[1]+data[2]+data[3]+data[4]+data[5]);
        return data;
    }
    // 读取强度指令 todo 待定
    public static byte[] getIntensityReadInstruct(){
        byte[] data =new byte[]{start,0x00,0x00,Control.READ.value,Type.INTENSITY.value,0x00,end};
        data[1] = (byte)(0x00+data.length);
        data[6] = (byte) (data[0]+data[1]+data[2]+data[3]+data[4]+data[5]);
        return data;
    }
    // 设置模式指令
    public static byte[] getModeSetInstruct(int value){
        byte[] data =new byte[]{start,0x00,(byte) Protocol.Data.MODE.getMode(value),Control.WRITE.value,Type.MODE.value,Protocol.Data.MODE.getMode(value),end};
        data[1] = (byte)(0x00+data.length);
        data[6] = (byte) (data[0]+data[1]+data[2]+data[3]+data[4]+data[5]);
        return data;
    }
    // 读取模式指令 todo 待定
    public static byte[] getModeGetInstruct(){
        byte[] data =new byte[]{start,0x00,0x00,Control.READ.value,Type.MODE.value,0x00,end};
        data[1] = (byte)(0x00+data.length);
        data[6] = (byte) (data[0]+data[1]+data[2]+data[3]+data[4]+data[5]);
        return data;
    }
    //读取电量指令
    public static byte[] getElectricGetInstruct(){
        byte[] data = new byte[]{start,0x00,0x00,Control.READ.value,Type.ELECTRIC.value,0x00,end};
        data[1] = (byte)(0x00+data.length);
        data[6] = (byte) (data[0]+data[1]+data[2]+data[3]+data[4]+data[5]);
        return data;
    }
}
