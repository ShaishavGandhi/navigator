package com.shaishavgandhi.navigator.sample;

import android.util.Size;
import android.util.SizeF;
import android.util.SparseArray;

import androidx.fragment.app.Fragment;
import com.shaishavgandhi.navigator.Extra;

import java.util.ArrayList;

public class TestJavaFragment extends Fragment {
    @Extra String javaString;
    @Extra long javaLong;
    @Extra long[] javaLongArray;
    @Extra Long javaBigLong;
    @Extra int javaInt;
    @Extra int[] javaIntArray;
    @Extra Integer javaBigInt;
    @Extra Double javaBigDouble;
    @Extra double javaDouble;
    @Extra double[] javaDoubleArray;
    @Extra Double[] javaBigDoubleArray;
    @Extra float javaFloat;
    @Extra float[] javaFloatArray;
    @Extra Float javaBigFloat;
    @Extra Float[] javaBigFloatArray;
    @Extra byte javaByte;
    @Extra byte[] javaByteArray;
    @Extra Byte javaBigByte;
    @Extra Byte[] javaBigByteArray;
    @Extra short javaShort;
    @Extra Short javaBigShort;
    @Extra short[] javaShortArray;
    @Extra Short[] javaBigShortArray; // Clever movie reference
    @Extra char javaChar;
    @Extra Character javaBigChar;
    @Extra char[] javaCharArray;
    @Extra Character[] javaBigCharArray;
    @Extra boolean javaBool;
    @Extra Boolean javaBigBool;
    @Extra boolean[] javaBoolArray;
    @Extra Boolean[] javaBigBoolArray;
    @Extra CharSequence javaCharSequence;
    @Extra CharSequence[] javaCharSequenceArray;
    @Extra ArrayList<CharSequence> javaCharSequenceList;
    @Extra Size androidSize;
    @Extra SizeF androidSizeF;
    @Extra SampleSerializable sampleSerializable;
    @Extra KTParcelable javaParcelable;
    @Extra ArrayList<KTParcelable> javaParcelableArrayList;
    @Extra KTParcelable[] javaParcelableArray;
    @Extra SparseArray<KTParcelable> javaParcelableSparseArray;
}
