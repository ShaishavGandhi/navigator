package com.shaishavgandhi.navigator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public final class FileWriter {

    private static final ClassName CONTEXT_CLASSNAME = ClassName.get("android.content", "Context");
    private static final ClassName INTENT_CLASSNAME = ClassName.get("android.content", "Intent");
    private static final ClassName BUNDLE_CLASSNAME = ClassName.get("android.os", "Bundle");
    private static final ClassName ACTIVITY_CLASSNAME = ClassName.get("android.app", "Activity");

    private static final ClassName NULLABLE = ClassName.bestGuess("android.support.annotation.Nullable");
    private static final ClassName NONNULL = ClassName.bestGuess("android.support.annotation" +
            ".NonNull");

    private static final String SERIALIZABLE = "Serializable";
    private static final String PARCELABLE = "Parcelable";
    private static final String FLAGS = "flags";

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
        put("android.os.Parcelable", "Parcelable");
        put("java.util.ArrayList<android.os.Parcelable>", "ParcelableArrayList");
        // TODO: Add support for CharSequenceArrayList


    }};

    private LinkedHashMap<ClassName, LinkedHashSet<Element>> annotationsPerClass;
    private Types typeUtils;
    private Elements elementUtils;

    public FileWriter(Types typeUtils, Elements elementUtils, LinkedHashMap<ClassName, LinkedHashSet<Element>> annotationsPerClass) {
        this.typeUtils = typeUtils;
        this.elementUtils = elementUtils;
        this.annotationsPerClass = annotationsPerClass;
    }

    public JavaFile writeFile() {
        TypeSpec.Builder navigator = TypeSpec.classBuilder("Navigator")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        for (Map.Entry<ClassName, LinkedHashSet<Element>> item : annotationsPerClass.entrySet()) {
            ClassName activity = item.getKey();
            LinkedHashSet<Element> annotations = item.getValue();
            writeBuilder(navigator, activity, annotations);
            MethodSpec bindMethod = getBindMethod(activity, annotations);
            navigator.addMethod(bindMethod);
        }

        return JavaFile.builder("com.shaishavgandhi.navigator", navigator.build())
                .build();
    }

    private MethodSpec getBindMethod(ClassName activity, LinkedHashSet<Element> annotations) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("bind")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                .addParameter(activity, "activity");
        builder.addStatement("$T bundle = $L.getIntent().getExtras()", BUNDLE_CLASSNAME,
                "activity");

        builder.beginControlFlow("if (bundle != null)");
        for (Element element: annotations) {
            Set<Modifier> modifiers = element.getModifiers();


            TypeName name = TypeName.get(element.asType());
            String varName = element.getSimpleName().toString();
            builder.beginControlFlow("if ($L.containsKey(\"$L\"))", "bundle", varName);

            String extraName = getExtraTypeName(element.asType());
            if (extraName == null) {
                // Add casting for serializable
                builder.addStatement("$T $L = ($T) bundle.get(\"$L\")", name, varName, name, varName);
            } else {
                builder.addStatement("$T $L = bundle.get" + extraName + "(\"$L\")", name, varName,
                        varName);
            }

            if (!modifiers.contains(Modifier.PUBLIC)) {
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

    private void writeBuilder(TypeSpec.Builder navigator, ClassName activity, LinkedHashSet<Element> elements) {
        String activityName = activity.simpleName();
        TypeSpec.Builder builder = TypeSpec.classBuilder(activityName + "Builder")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);

        builder.addField(FieldSpec.builder(TypeName.INT, FLAGS)
                .initializer("$L", -1)
                .build());

        ClassName builderClass = ClassName.bestGuess(activityName + "Builder");

        // Constructor
        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE);

        // Set intent flags
        MethodSpec.Builder flagBuilder = MethodSpec.methodBuilder("setFlags")
                .addParameter(ParameterSpec.builder(TypeName.INT, "flags", Modifier.FINAL).build())
                .addModifiers(Modifier.PUBLIC)
                .returns(builderClass)
                .addStatement("this.$1L = $1L", FLAGS)
                .addStatement("return this");

        builder.addMethod(flagBuilder.build());

        // Static method to prepare activity
        MethodSpec.Builder prepareMethodBuilder = getPrepareActivityMethod(activityName,
                builderClass);

        // Bundle builder
        MethodSpec.Builder bundleBuilder = getExtrasBundle(activityName);

        // Start activity
        MethodSpec.Builder startActivityBuilder = getStartActivityMethod(activityName);

        // Start for result
        MethodSpec.Builder startForResultBuilder = getStartForResultMethod(activityName);
        // Start result with extras
        MethodSpec.Builder startResultExtrasBuilder = getStartForResultWithExtras(activityName);

        // TODO: There must be a better way for this with JavaPoet. Right now
        // I manually append each parameter and remove commas and close the bracket
        StringBuilder returnStatement = new StringBuilder("return new $T(");

        for (Element element: elements) {
            TypeMirror typeMirror = element.asType();
            if (typeMirror == null) {
                continue;
            }
            // Add as field parameter
            String name = element.getSimpleName().toString();
            builder.addField(TypeName.get(typeMirror), name, Modifier.PRIVATE);

            // Add to constructor
            ParameterSpec parameter = getParameter(element);
            constructorBuilder.addParameter(parameter);
            constructorBuilder.addStatement("this.$L = $L", parameter.name, parameter.name);

            // Add to static prepare method
            prepareMethodBuilder.addParameter(parameter);

            // Append to return statement
            returnStatement.append(parameter.name);
            returnStatement.append(", ");

            // Put to bundle
            bundleBuilder.addStatement("intent.putExtra(\"$L\", $L)", parameter.name, parameter.name);
        }

        // Sanitize return statement
        returnStatement.deleteCharAt(returnStatement.length() - 2);
        returnStatement.deleteCharAt(returnStatement.length() - 1);
        returnStatement.append(")");
        prepareMethodBuilder.addStatement(returnStatement.toString(), builderClass);

        bundleBuilder.addStatement("return intent.getExtras()");
        MethodSpec bundle  = bundleBuilder.build();

        addOptionalAttributes(startActivityBuilder);

        startActivityBuilder.addStatement("intent.putExtras($N())", bundle);
        startActivityBuilder.addStatement("$L.startActivity($L)", "context", "intent");

        startForResultBuilder.addStatement("intent.putExtras($N())", bundle);
        startForResultBuilder.addStatement("$L.startActivityForResult($L, $L)", "activity",
                "intent", "requestCode");

        startResultExtrasBuilder.addStatement("intent.putExtras($N())", bundle);
        startResultExtrasBuilder.addStatement("$L.startActivityForResult($L, $L, $L)", "activity",
                "intent", "requestCode", "extras");

        builder.addMethod(startActivityBuilder.build());
        builder.addMethod(startForResultBuilder.build());
        builder.addMethod(startResultExtrasBuilder.build());
        builder.addMethod(bundle);
        TypeSpec builderInnerClass = builder.addMethod(constructorBuilder.build()).build();

        navigator.addType(builderInnerClass);
        navigator.addMethod(prepareMethodBuilder.build());
    }

    private MethodSpec.Builder getExtrasBundle(String activityName) {
        // TODO: This is a hack. We are creating an intent and then skipping type safety
        // by using intent.putExtra() and then getting bundle using intent.getExtras();
        // Will have to figure out how to know if element is ParcelableaArrayList
        // and other types. This isn't that bad since if the user uses the appropriate
        // types, it should just work. Unfortunately, if they don't, this will
        // result in a run time exception while casting and we want to avoid that since
        // that's the whole point of using annotation processors
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("getBundle")
                .addModifiers(Modifier.PUBLIC);
        methodBuilder.addStatement("$T intent = new $T()", INTENT_CLASSNAME,
                INTENT_CLASSNAME);
        methodBuilder.returns(BUNDLE_CLASSNAME);
        return methodBuilder;
    }

    private MethodSpec.Builder getStartActivityMethod(String activityName) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("start")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(CONTEXT_CLASSNAME, "context");
        methodBuilder.addStatement("$T intent = new $T($L, $L)", INTENT_CLASSNAME,
                INTENT_CLASSNAME, "context", activityName + ".class");
        return methodBuilder;
    }

    private MethodSpec.Builder getStartForResultMethod(String activityName) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("startForResult")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ACTIVITY_CLASSNAME, "activity")
                .addParameter(TypeName.INT, "requestCode");
        methodBuilder.addStatement("$T intent = new $T($L, $L)", INTENT_CLASSNAME,
                INTENT_CLASSNAME, "activity", activityName + ".class");
        return methodBuilder;
    }

    private MethodSpec.Builder getStartForResultWithExtras(String activityName) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("startForResult")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ACTIVITY_CLASSNAME, "activity")
                .addParameter(TypeName.INT, "requestCode")
                .addParameter(ParameterSpec.builder(BUNDLE_CLASSNAME, "extras", Modifier.FINAL)
                .addAnnotation(NULLABLE).build());
        methodBuilder.addStatement("$T intent = new $T($L, $L)", INTENT_CLASSNAME,
                INTENT_CLASSNAME, "activity", activityName + ".class");
        return methodBuilder;
    }

    private MethodSpec.Builder getPrepareActivityMethod(String activityName, ClassName builderClass) {
        MethodSpec.Builder prepareMethodBuilder = MethodSpec.methodBuilder("prepare" +
                activityName);
        prepareMethodBuilder.addModifiers(Modifier.STATIC, Modifier.FINAL, Modifier.PUBLIC);
        prepareMethodBuilder.returns(builderClass);
        return prepareMethodBuilder;
    }

    private void addOptionalAttributes(MethodSpec.Builder builder) {
        builder.beginControlFlow("if ($L != -1)", FLAGS);
        builder.addStatement("$L.setFlags($L)", "intent", FLAGS);
        builder.endControlFlow();
    }

    private ParameterSpec getParameter(Element element) {
        TypeMirror typeMirror = element.asType();
        String name = element.getSimpleName().toString();
        ParameterSpec.Builder parameterBuilder = ParameterSpec.builder(TypeName.get(typeMirror),
                name);
        parameterBuilder.addModifiers(Modifier.FINAL);

        if (!typeMirror.getKind().isPrimitive()) {
            parameterBuilder.addAnnotation(NONNULL);
        }

        return parameterBuilder.build();
    }

    private String getExtraTypeName(TypeMirror typeMirror) {
        TypeName typeName = TypeName.get(typeMirror);
        return typeMapper.get(typeName.toString());
    }

    private boolean isParcelable(Types typeUtils, Elements elementUtils, TypeMirror typeMirror) {
        return typeUtils.isAssignable(typeMirror, elementUtils.getTypeElement("android.os.Parcelable")
                .asType());
    }

    private boolean isParcelableList(Types typeUtils, Elements elementUtils, TypeMirror typeMirror) {
        DeclaredType type = typeUtils.getDeclaredType(elementUtils.getTypeElement("java.util" +
                        ".ArrayList"),
                elementUtils.getTypeElement("android.os.Parcelable").asType());
        return typeUtils.isAssignable(typeMirror, type);
    }

    private boolean isSerializable(Types typeUtils, Elements elementUtils, TypeMirror typeMirror) {
        return typeUtils.isAssignable(typeMirror, elementUtils.getTypeElement("java.io.Serializable")
                .asType());
    }

}
