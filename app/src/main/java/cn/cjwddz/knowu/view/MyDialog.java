package cn.cjwddz.knowu.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import cn.cjwddz.knowu.common.utils.MyUtils;

/**
 * Created by Administrator on 2018/8/6.
 */
// TODO: 2018/8/7 完善/封装自定义Dialog
public class MyDialog extends Dialog {
    private Context context;
    private int height,width;
    private boolean canceltouchout;
    private View view;

    public MyDialog(Builder builder){
        super(builder.context);
        context = builder.context;
        height = builder.height;
        width = builder.width;
        canceltouchout = builder.canceltouchout;
        view = builder.view;
    }

    public MyDialog(Builder builder,int resStyle){
        super(builder.context,resStyle);
        context = builder.context;
        height = builder.height;
        width = builder.width;
        canceltouchout = builder.canceltouchout;
        view = builder.view;
    }

    /**
    public MyDialog(@NonNull Context context) {
        super(context);
    }

    public MyDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected MyDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       //View view = View.inflate(getContext(), R.layout.dialog,null);
        setContentView(view);
        //是否响应dialog外部点击事件
        setCanceledOnTouchOutside(canceltouchout);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = width;
        params.height = height;
        window.setAttributes(params);
    }

    public View getView(){
        return view;
    }
    public static final class Builder{
        private Context context;
        private int height,width;
        private boolean canceltouchout;
        private View view;
        private int resStyle = -1;

        public Builder(Context context){
            this.context = context;
        }

        public Builder setView(int resView){
            view = LayoutInflater.from(context).inflate(resView,null);
            return this;
        }

        //通过px，dp，dimen资源设置Dialog高度
        public Builder setHeightpx(int value){
            height = value;
            return this;
        }
        public Builder setHeihgtdp(int value){
            height = MyUtils.dip2px(context,value );
            return this;
        }
        public Builder setHeightDimenRes(int dimenRes){
            height = context.getResources().getDimensionPixelOffset(dimenRes);
            return this;
        }

        //通过px，dp，dimen资源设置Dialog宽度
        public Builder setWidthpx(int value){
            width = value;
            return this;
        }
        public Builder setWidthdp(int value){
            width = MyUtils.dip2px(context,value );
            return this;
        }
        public Builder setWidthDimenRes(int dimenRes){
            width = context.getResources().getDimensionPixelOffset(dimenRes);
            return this;
        }

        //设置Dialog样式
        public Builder setStyle(int resStyle){
            this.resStyle = resStyle;
            return this;
        }

        //
        public Builder setCancelTouchout(boolean canceltouchout){
            this.canceltouchout = canceltouchout;
            return this;
        }

        //设置监听器
        public Builder addViewOnclickListener(int viewRse,View.OnClickListener listener){
            view.findViewById(viewRse).setOnClickListener(listener);
            return this;
        }

        //设置监听器
        public void addOnclickListener(int viewRse,View.OnClickListener listener){
            view.findViewById(viewRse).setOnClickListener(listener);
        }


        public MyDialog build(){
            if(resStyle != -1){
                return new MyDialog(this,resStyle);
            }else{
             return new MyDialog(this);
            }
        }
    }
}
