package com.shaishavgandhi.navigator;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
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

import static com.shaishavgandhi.navigator.StringUtils.capitalize;

final class FileWriter {

    private static final ClassName CONTEXT_CLASSNAME = ClassName.get("android.content", "Context");
    private static final ClassName INTENT_CLASSNAME = ClassName.get("android.content", "Intent");
    private static final ClassName BUNDLE_CLASSNAME = ClassName.get("android.os", "Bundle");
    private static final ClassName ACTIVITY_CLASSNAME = ClassName.get("android.app", "Activity");


    private static final String SERIALIZABLE = "Serializable";
    private static final String PARCELABLE = "Parcelable";
    private static final String FLAGS = "flags";

    private HashMap<String, String> typeMapper = new HashMap<String, String>(){{
        put("java.lang.String", "String");
        put("java.lang.String[]", "StringArray");
        put("java.util.ArrayList<java.lang.String>", "StringArrayList");
        put("java.lang.Integer", "Int");
        put("int", "Int");
        put("int[]", "IntArray");
        put("java.util.ArrayList<java.lang.Integer>", "IntegerArrayList");
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
        put("java.util.ArrayList<java.lang.CharSequence>", "CharSequenceArrayList");
        put("android.util.Size", "Size");
        put("android.util.SizeF", "SizeF");
        put("boolean", "Boolean");
        put("boolean[]", "BooleanArray");
        put("java.lang.Boolean", "Boolean");
        put("java.lang.Boolean[]", "BooleanArray");
        put("android.os.Parcelable", "Parcelable");
        put("java.util.ArrayList<android.os.Parcelable>", "ParcelableArrayList");
    }};

    private LinkedHashMap<ClassName, LinkedHashSet<Element>> annotationsPerClass;
    private Types typeUtils;
    private Elements elementUtils;

    FileWriter(Types typeUtils, Elements elementUtils, LinkedHashMap<ClassName, LinkedHashSet<Element>> annotationsPerClass) {
        this.typeUtils = typeUtils;
        this.elementUtils = elementUtils;
        this.annotationsPerClass = annotationsPerClass;
    }

    JavaFile writeFile() {
        TypeSpec.Builder navigator = TypeSpec.classBuilder("Navigator")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        for (Map.Entry<ClassName, LinkedHashSet<Element>> item : annotationsPerClass.entrySet()) {
            ClassName className = item.getKey();
            LinkedHashSet<Element> annotations = item.getValue();

            writeBuilder(navigator, className, annotations);
            MethodSpec bindMethod = getBindMethod(className, annotations);
            navigator.addMethod(bindMethod);
        }

        return JavaFile.builder("com.shaishavgandhi.navigator", navigator.build())
                .build();
    }

    private MethodSpec getBindMethod(ClassName activity, LinkedHashSet<Element> annotations) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("bind")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                .addParameter(activity, "binder");

        if (isActivity(activity)) {
            builder.addStatement("$T bundle = $L.getIntent().getExtras()", BUNDLE_CLASSNAME,
                    "binder");
        } else if (isFragment(activity)) {
            builder.addStatement("$T bundle = $L.getArguments()", BUNDLE_CLASSNAME,
                    "binder");
        }

        builder.beginControlFlow("if (bundle != null)");
        for (Element element: annotations) {
            Set<Modifier> modifiers = element.getModifiers();


            TypeName name = TypeName.get(element.asType());
            String varName = element.getSimpleName().toString();
            builder.beginControlFlow("if ($L.containsKey(\"$L\"))", "bundle", varName);

            String extraName = getExtraTypeName(element.asType());
            if (extraName == null) {
                String field = element.asType().toString();
            }
            if (extraName == null) {
                // Add casting for serializable
                builder.addStatement("$T $L = ($T) bundle.get(\"$L\")", name, varName, name, varName);
            } else {
                builder.addStatement("$T $L = bundle.get" + extraName + "(\"$L\")", name, varName,
                        varName);
            }

            if (!modifiers.contains(Modifier.PUBLIC)) {
                // Use getter and setter
                builder.addStatement("$L.set$L($L)", "binder", capitalize(varName), varName);

            } else {
                builder.addStatement("$L.$L = $L", "binder", varName, varName);
            }
            builder.endControlFlow();
        }
        builder.endControlFlow();


        return builder.build();
    }

    private boolean isFragment(ClassName className) {
        TypeMirror supportFragment = elementUtils.getTypeElement("android.support.v4.app.Fragment")
                .asType();
        TypeMirror fragment = elementUtils.getTypeElement("android.app.Fragment")
                .asType();
        // TODO: Add support for androidx.fragment
        TypeMirror currentClass = elementUtils.getTypeElement(className.toString()).asType();
        return typeUtils.isSubtype(currentClass, fragment) || typeUtils.isSubtype(currentClass,
                supportFragment);

    }

    private boolean isActivity(ClassName className) {
        TypeMirror activity = elementUtils.getTypeElement("android.app.Activity").asType();
        TypeMirror currentClass = elementUtils.getTypeElement(className.toString()).asType();
        return typeUtils.isSubtype(currentClass, activity);
    }

    private void writeBuilder(TypeSpec.Builder navigator, ClassName activity, LinkedHashSet<Element> elements) {
        String activityName = activity.simpleName();
        TypeSpec.Builder builder = TypeSpec.classBuilder(activityName + "Builder")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);

        builder.addField(FieldSpec.builder(TypeName.INT, FLAGS)
                .initializer("$L", -1)
                .build());

        builder.addField(FieldSpec.builder(BUNDLE_CLASSNAME, "extras").build());

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

        // Static method to prepare activity
        MethodSpec.Builder prepareMethodBuilder = getPrepareActivityMethod(activityName,
                builderClass);

        // Bundle builder
        MethodSpec.Builder bundleBuilder = getExtrasBundle(activityName);

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
            AnnotationSpec nullability = getNullabilityFor(element);
            builder.addField(FieldSpec.builder(TypeName.get(typeMirror), name, Modifier.PRIVATE)
                    .addAnnotation(nullability)
                    .build());

            // Add to constructor
            ParameterSpec parameter = getParameter(element);

            AnnotationSpec nullable = AnnotationSpec.builder(ClassName.get(Nullable.class)).build();
            if (!parameter.annotations.contains(nullable)) {
                constructorBuilder.addParameter(parameter);
                constructorBuilder.addStatement("this.$L = $L", parameter.name, parameter.name);

                // Add to static prepare method
                prepareMethodBuilder.addParameter(parameter);

                // Append to return statement
                returnStatement.append(parameter.name);
                returnStatement.append(", ");
            } else {
                addFieldToBuilder(builder, element, builderClass);
            }

            // Put to bundle
            bundleBuilder.addStatement("intent.putExtra(\"$L\", $L)", parameter.name, parameter.name);
        }

        // Sanitize return statement
        if (returnStatement.charAt(returnStatement.length() - 1) == ' ') {
            returnStatement.deleteCharAt(returnStatement.length() - 2);
            returnStatement.deleteCharAt(returnStatement.length() - 1);
        }
        returnStatement.append(")");
        prepareMethodBuilder.addStatement(returnStatement.toString(), builderClass);

        bundleBuilder.beginControlFlow("if ($L != null)", "extras");
        bundleBuilder.addStatement("intent.putExtras($L)", "extras");
        bundleBuilder.endControlFlow();
        bundleBuilder.addStatement("return intent.getExtras()");
        MethodSpec bundle  = bundleBuilder.build();

        // Start activity
        MethodSpec.Builder startActivityBuilder = getStartActivityMethod(activityName, bundle);

        // Start activity with extras
        MethodSpec.Builder startActivityExtrasBuilder = getStartActivityWithExtras(activityName,
                bundle);

        // Start for result
        MethodSpec.Builder startForResultBuilder = getStartForResultMethod(activityName, bundle);

        // Start result with extras
        MethodSpec.Builder startResultExtrasBuilder = getStartForResultWithExtras(activityName,
                bundle);

        MethodSpec.Builder setExtrasBuilder = getExtrasMethod(builderClass);

        if (isActivity(activity)) {
            // Add activity specific methods
            builder.addMethod(startActivityBuilder.build());
            builder.addMethod(startForResultBuilder.build());
            builder.addMethod(startResultExtrasBuilder.build());
            builder.addMethod(startActivityExtrasBuilder.build());
            builder.addMethod(flagBuilder.build());
        }
        builder.addMethod(bundle);
        TypeSpec builderInnerClass = builder.addMethod(constructorBuilder.build()).build();

        navigator.addType(builderInnerClass);
        navigator.addMethod(prepareMethodBuilder.build());
    }

    private MethodSpec.Builder getExtrasMethod(ClassName builderClass) {
        return MethodSpec.methodBuilder("setExtras")
                .addModifiers(Modifier.PUBLIC)
                .returns(builderClass)
                .addStatement("this.$L = $L", "extras", "extras")
                .addStatement("return this");
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

    private MethodSpec.Builder getStartActivityMethod(String activityName, MethodSpec bundle) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("start")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(CONTEXT_CLASSNAME, "context");
        methodBuilder.addStatement("$T intent = new $T($L, $L)", INTENT_CLASSNAME,
                INTENT_CLASSNAME, "context", activityName + ".class");
        // Put extras
        methodBuilder.addStatement("intent.putExtras($N())", bundle);

        // Set flags if they exist
        addOptionalAttributes(methodBuilder);

        // Start activity
        methodBuilder.addStatement("$L.startActivity($L)", "context", "intent");
        return methodBuilder;
    }

    private MethodSpec.Builder getStartActivityWithExtras(String activityName, MethodSpec bundle) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("startWithExtras")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(CONTEXT_CLASSNAME, "context")
                .addParameter(BUNDLE_CLASSNAME, "extras");
        methodBuilder.addStatement("$T intent = new $T($L, $L)", INTENT_CLASSNAME,
                INTENT_CLASSNAME, "context", activityName + ".class");
        // Put extras
        methodBuilder.addStatement("intent.putExtras($N())", bundle);
        // Set flags if any
        addOptionalAttributes(methodBuilder);
        // Start activity
        methodBuilder.addStatement("$L.startActivity($L, $L)", "context", "intent", "extras");
        return methodBuilder;
    }

    private void addFieldToBuilder(TypeSpec.Builder builder, Element element, ClassName builderClass) {
        String variableName = element.getSimpleName().toString();
        MethodSpec.Builder setter = MethodSpec.methodBuilder("set" + capitalize(variableName))
                .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                .addParameter(TypeName.get(element.asType()), variableName)
                .addStatement("this.$L = $L", variableName, variableName)
                .returns(builderClass)
                .addStatement("return this");

        builder.addMethod(setter.build());
    }

    private MethodSpec.Builder getStartForResultMethod(String activityName, MethodSpec bundle) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("startForResult")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ACTIVITY_CLASSNAME, "activity")
                .addParameter(TypeName.INT, "requestCode");
        methodBuilder.addStatement("$T intent = new $T($L, $L)", INTENT_CLASSNAME,
                INTENT_CLASSNAME, "activity", activityName + ".class");

        // Put extras
        methodBuilder.addStatement("intent.putExtras($N())", bundle);
        //Add flags if any
        addOptionalAttributes(methodBuilder);
        // Start for result
        methodBuilder.addStatement("$L.startActivityForResult($L, $L)", "activity",
                "intent", "requestCode");
        return methodBuilder;
    }

    private MethodSpec.Builder getStartForResultWithExtras(String activityName, MethodSpec bundle) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("startForResult")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ACTIVITY_CLASSNAME, "activity")
                .addParameter(TypeName.INT, "requestCode")
                .addParameter(ParameterSpec.builder(BUNDLE_CLASSNAME, "extras", Modifier.FINAL)
                .addAnnotation(Nullable.class).build());
        methodBuilder.addStatement("$T intent = new $T($L, $L)", INTENT_CLASSNAME,
                INTENT_CLASSNAME, "activity", activityName + ".class");

        // Put extras
        methodBuilder.addStatement("intent.putExtras($N())", bundle);
        // Set flags
        addOptionalAttributes(methodBuilder);
        // Start activity for result
        methodBuilder.addStatement("$L.startActivityForResult($L, $L, $L)", "activity",
                "intent", "requestCode", "extras");
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
        ParameterSpec.Builder parameterBuilder = ParameterSpec.builder(TypeName.get(typeMirror), name);
        parameterBuilder.addModifiers(Modifier.FINAL);

        parameterBuilder.addAnnotation(getNullabilityFor(element));

        return parameterBuilder.build();
    }

    private @Nullable AnnotationSpec getNullabilityFor(Element element) {
        TypeMirror typeMirror = element.asType();
        if (!typeMirror.getKind().isPrimitive()) {
            if (element.getAnnotation(Nullable.class) == null) {
                return AnnotationSpec.builder(ClassName.get(NonNull.class)).build();
            } else {
                return AnnotationSpec.builder(ClassName.get(Nullable.class)).build();
            }
        }
        return null;
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
