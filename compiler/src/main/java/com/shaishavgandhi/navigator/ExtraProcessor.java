package com.shaishavgandhi.navigator;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

@AutoService(Processor.class)
public class ExtraProcessor extends AbstractProcessor {

    private static final ClassName CONTEXT_CLASSNAME = ClassName.get("android.content", "Context");
    private static final ClassName INTENT_CLASSNAME = ClassName.get("android.content", "Intent");
    private static final ClassName BUNDLE_CLASSNAME = ClassName.get("android.os", "Bundle");

    private HashMap<String, String> typeMapper = new HashMap<String, String>(){{
        put("java.lang.String", "String");
        put("java.lang.String[]", "StringArray");
        put("java.lang.Integer", "Int");
        put("int", "Int");
        put("int[]", "IntArray");
        put("java.lang.Long","Long");
        put("long", "Long");
        put("long[]", "LongArray");
        put("double", "Double");
        put("java.lang.Double", "Double");
        put("double[]", "DoubleArray");
        put("float", "Float");
        put("java.lang.Float","Float");
        put("float[]", "FloatArray");
        put("byte", "Byte");
        put("byte[]", "ByteArray");
        put("short", "Short");
        put("short[]", "ShortArray");
        put("char", "Char");
        put("char[]", "CharArray");
        put("java.lang.CharSequence", "CharSequence");
        put("java.lang.CharSequence[]", "CharSequenceArray");
        put("android.util.Size", "Size");
        put("android.util.SizeF", "SizeF");
        put("boolean", "Boolean");
        put("boolean[]", "BooleanArray");
        put("java.lang.Boolean", "Boolean");
        put("java.lang.Boolean[]", "BooleanArray");
        // TODO: Add support for CharSequenceArrayList


    }};

    private LinkedHashMap<String, Set<Element>> annotationsPerClass;
    private Types typeUtils;
    private Elements elementUtils;

    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnvironment.getFiler();
        annotationsPerClass = new LinkedHashMap<>();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        for (Element element : roundEnvironment.getElementsAnnotatedWith(Extra.class)) {
            String classname = element.getEnclosingElement().getSimpleName().toString();
            if (annotationsPerClass.containsKey(classname)) {
                Set<Element> annotations = annotationsPerClass.get(classname);
                annotations.add(element);
                annotationsPerClass.put(classname, annotations);
            } else {
                Set<Element> annotations = new HashSet<>();
                annotations.add(element);
                annotationsPerClass.put(classname, annotations);
            }
        }


        TypeSpec.Builder navigator = TypeSpec.classBuilder("Navigator")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        for (Map.Entry<String, Set<Element>> item : annotationsPerClass.entrySet()) {
            String activity = item.getKey();
            Set<Element> annotations = item.getValue();
            MethodSpec method = getMethod(activity, annotations);
            MethodSpec bindMethod = getBindMethod(activity, annotations);
            navigator.addMethod(method);
            navigator.addMethod(bindMethod);
        }

        JavaFile javaFile = JavaFile.builder("com.shaishavgandhi.navigator", navigator.build())
                .build();

        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    private MethodSpec getBindMethod(String activity, Set<Element> annotations) {
        ClassName activityClass = ClassName.bestGuess(activity);
        MethodSpec.Builder builder = MethodSpec.methodBuilder("bind")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                .addParameter(activityClass, "activity");
        builder.addStatement("$T bundle = $L.getIntent().getExtras()", BUNDLE_CLASSNAME,
                "activity");

        builder.beginControlFlow("if (bundle != null)");
        for (Element element: annotations) {
            Set<Modifier> modifiers = element.getModifiers();


            TypeName name = TypeName.get(element.asType());
            String varName = element.getSimpleName().toString();
            builder.beginControlFlow("if ($L.containsKey(\"$L\"))", "bundle", varName);
            builder.addStatement("$T $L = bundle.get" + getExtraTypeName(element.asType()) + "" +
                            "(\"$L\")",
                    name, varName, varName);
            if (modifiers.contains(Modifier.PRIVATE)) {
                // Use getter and setter
                builder.addStatement("$L.set$L($L)", "activity", varName.substring(0, 1).toUpperCase() +
                        varName.substring(1), varName);

            } else {
                builder.addStatement("$L.$L = $L", "activity", varName, varName);
            }
            builder.endControlFlow();
        }
        builder.endControlFlow();


        return builder.build();
    }

    private MethodSpec getMethod(String activity, Set<Element> elements) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("start" + activity)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                .addParameter(CONTEXT_CLASSNAME, "context");

        builder.addStatement("$T intent = new $T($L, $L)", INTENT_CLASSNAME,
                INTENT_CLASSNAME, "context",
                activity + ".class");

        for (Element element: elements) {
            TypeMirror typeMirror = element.asType();
            if (typeMirror == null) {
                continue;
            }
//            if (!isValidExtra(element)) {
//                throw new IllegalStateException("Can't set field to Bundle");
//            }
            String name = element.getSimpleName().toString();
            ParameterSpec.Builder parameterBuilder = ParameterSpec.builder(TypeName.get
                    (typeMirror), name);

            if (!typeMirror.getKind().isPrimitive()) {
                parameterBuilder.addAnnotation(ClassName.bestGuess("android.support.annotation" +
                        ".NonNull"));
            }
            builder.addParameter(parameterBuilder.build());
            builder.addStatement("intent.putExtra(\"$L\", $L)", name, name);
        }

        builder.addStatement("$L.startActivity($L)", "context", "intent");
        return builder.build();
    }

    private boolean isValidExtra(Element element) {
        return !(element.asType().getKind() == TypeKind.DECLARED
                && !typeUtils.isSubtype(element.asType(), elementUtils.getTypeElement("java.io.Serializable").asType())
                && !typeUtils.isSubtype(element.asType(), elementUtils.getTypeElement("android.os" +
                ".Parcelable").asType()));
    }

    private String getExtraTypeName(TypeMirror typeMirror) {
        TypeName typeName = TypeName.get(typeMirror);
        String type = typeMapper.get(typeName.toString());
        if (type == null) {
            if (isSerializable(typeUtils, elementUtils, typeMirror)) {
                type = "Serializable";
            } else if (isParcelable(typeUtils, elementUtils, typeMirror)) {
                type = "Parcelable";
            }
        }
        return type;
    }

    private boolean isParcelable(Types typeUtils, Elements elementUtils, TypeMirror typeMirror) {
        return typeUtils.isSubtype(typeMirror, elementUtils.getTypeElement("android.os.Parcelable")
                .asType());
    }

    private boolean isSerializable(Types typeUtils, Elements elementUtils, TypeMirror typeMirror) {
        return typeUtils.isSubtype(typeMirror, elementUtils.getTypeElement("java.io.Serializable")
                .asType());
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> set = new HashSet<>();
        set.add(Extra.class.getCanonicalName());
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }
}
