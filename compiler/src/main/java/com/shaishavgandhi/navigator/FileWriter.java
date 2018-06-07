package com.shaishavgandhi.navigator;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import static com.shaishavgandhi.navigator.StringUtils.capitalize;

final class FileWriter {

    private static final ClassName CONTEXT_CLASSNAME = ClassName.get("android.content", "Context");
    private static final ClassName INTENT_CLASSNAME = ClassName.get("android.content", "Intent");
    private static final ClassName BUNDLE_CLASSNAME = ClassName.get("android.os", "Bundle");
    private static final ClassName ACTIVITY_CLASSNAME = ClassName.get("android.app", "Activity");
    private static final ClassName STRING_CLASS = ClassName.bestGuess("java.lang.String");

    private static final String SERIALIZABLE = "Serializable";
    private static final String PARCELABLE = "Parcelable";
    private static final String FLAGS = "flags";
    private static final String ACTION = "action";

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
        put("android.os.Parcelable[]", "ParcelableArray");
        put("java.util.ArrayList<android.os.Parcelable>", "ParcelableArrayList");
    }};

    private LinkedHashMap<ClassName, LinkedHashSet<Element>> annotationsPerClass;
    private Types typeUtils;
    private Elements elementUtils;
    private List<JavaFile> files = new ArrayList<>();
    private Messager messager;

    FileWriter(Types typeUtils, Elements elementUtils, LinkedHashMap<ClassName,
            LinkedHashSet<Element>> annotationsPerClass, Messager messager) {
        this.typeUtils = typeUtils;
        this.elementUtils = elementUtils;
        this.annotationsPerClass = annotationsPerClass;
        this.messager = messager;
    }

    protected void writeFiles() {
        TypeSpec.Builder navigator = TypeSpec.classBuilder("Navigator")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        for (Map.Entry<ClassName, LinkedHashSet<Element>> item : annotationsPerClass.entrySet()) {
            ClassName className = item.getKey();
            LinkedHashSet<Element> annotations = item.getValue();

            writeBuilder(navigator, className, annotations);
            MethodSpec bindMethod = getBindMethod(className, annotations);
            navigator.addMethod(bindMethod);
        }

        files.add(JavaFile.builder("com.shaishavgandhi.navigator", navigator.build())
                .build());
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
        } else {
            messager.printMessage(Diagnostic.Kind.ERROR, "@Extra can only be applied to fields in" +
                    " Activities or Fragments");
        }

        builder.beginControlFlow("if (bundle != null)");
        for (Element element: annotations) {
            Set<Modifier> modifiers = element.getModifiers();


            TypeName name = TypeName.get(element.asType());
            String varKey = getVariableKey(element);
            String varName = element.getSimpleName().toString();
            builder.beginControlFlow("if ($L.containsKey(\"$L\"))", "bundle", varKey);

            String extraName = getExtraTypeName(element.asType());
            if (extraName == null) {
                if (isSerializable(typeUtils, elementUtils, element.asType())) {
                    // Add casting for serializable
                    builder.addStatement("$T $L = ($T) bundle.getSerializable(\"$L\")", name,
                            varName, name, varKey);
                }
            } else {
                if (extraName.equals("ParcelableArray")) {
                    // Add extra casting. TODO: Refactor this to be more generic
                    builder.addStatement("$T $L = ($T) bundle.get" + extraName + "(\"$L\")", name,
                            varName, name, varKey);
                } else {
                    builder.addStatement("$T $L = bundle.get" + extraName + "(\"$L\")", name, varName,
                            varKey);
                }
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
        TypeMirror currentClass = elementUtils.getTypeElement(className.toString()).asType();
        boolean isFragment = false;
        if (elementUtils.getTypeElement("android.support.v4.app.Fragment") != null) {
            TypeMirror supportFragment = elementUtils.getTypeElement("android.support.v4.app.Fragment").asType();
            // TODO: Add support for androidx.fragment
            isFragment = typeUtils.isSubtype(currentClass, supportFragment);
        }
        if (elementUtils.getTypeElement("android.app.Fragment") != null && !isFragment) {
            messager.printMessage(Diagnostic.Kind.WARNING, "Got in");
            messager.printMessage(Diagnostic.Kind.WARNING, currentClass.toString());
            TypeMirror fragment = elementUtils.getTypeElement("android.app.Fragment").asType();
            isFragment = typeUtils.isSubtype(currentClass, fragment);
        }
        return isFragment;
    }

    private boolean isActivity(ClassName className) {
        if (elementUtils.getTypeElement("android.app.Activity") != null) {
            TypeMirror activity = elementUtils.getTypeElement("android.app.Activity").asType();
            TypeMirror currentClass = elementUtils.getTypeElement(className.toString()).asType();
            return typeUtils.isSubtype(currentClass, activity);
        }
        return false;
    }

    private void writeBuilder(TypeSpec.Builder navigator, ClassName activity, LinkedHashSet<Element> elements) {
        String activityName = activity.simpleName();
        TypeSpec.Builder builder = TypeSpec.classBuilder(activityName + "Builder")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        builder.addField(FieldSpec.builder(TypeName.INT, FLAGS)
                .addModifiers(Modifier.PRIVATE)
                .initializer("$L", -1)
                .build());

        builder.addField(FieldSpec.builder(STRING_CLASS, ACTION)
                .addModifiers(Modifier.PRIVATE)
                .build());

        builder.addField(FieldSpec.builder(BUNDLE_CLASSNAME, "extras")
                .addModifiers(Modifier.PRIVATE).build());

        ClassName builderClass = ClassName.bestGuess(activityName + "Builder");

        // Constructor
        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PROTECTED);

        // Set intent flags
        MethodSpec.Builder flagBuilder = setFlagsMethod(builderClass);

        // Set action
        MethodSpec.Builder setActionBuilder = setActionMethod(builderClass);

        // Static method to prepare activity
        MethodSpec.Builder prepareMethodBuilder = getPrepareActivityMethod(activityName,
                builderClass);

        // Bundle builder
        MethodSpec.Builder bundleBuilder = getExtrasBundle();

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

            String extraName = getExtraTypeName(element.asType());

            if (extraName == null) {
                // Put to bundle
                bundleBuilder.addStatement("bundle.putSerializable(\"$L\", $L)", getVariableKey(element),
                        parameter.name);
            } else {
                // Put to bundle
                bundleBuilder.addStatement("bundle.put" + extraName + "(\"$L\", $L)", getVariableKey(element),
                        parameter.name);
            }
        }

        // Sanitize return statement
        if (returnStatement.charAt(returnStatement.length() - 1) == ' ') {
            returnStatement.deleteCharAt(returnStatement.length() - 2);
            returnStatement.deleteCharAt(returnStatement.length() - 1);
        }
        returnStatement.append(")");
        prepareMethodBuilder.addStatement(returnStatement.toString(), builderClass);

        bundleBuilder.beginControlFlow("if ($L != null)", "extras");
        bundleBuilder.addStatement("bundle.putAll($L)", "extras");
        bundleBuilder.endControlFlow();
        bundleBuilder.addStatement("return bundle");
        MethodSpec bundle  = bundleBuilder.build();

        // Start activity
        MethodSpec.Builder startActivityBuilder = getStartActivityMethod(activity, bundle);

        // Start activity with extras
        MethodSpec.Builder startActivityExtrasBuilder = getStartActivityWithExtras(activityName,
                bundle);

        // Start for result
        MethodSpec.Builder startForResultBuilder = getStartForResultMethod(activityName, bundle);

        // Start result with extras
        MethodSpec.Builder startResultExtrasBuilder = getStartForResultWithExtras(activityName,
                bundle);

        MethodSpec.Builder setExtrasBuilder = getExtrasSetterMethod(builderClass);

        if (isActivity(activity)) {
            // Add activity specific methods
            builder.addMethod(startActivityBuilder.build());
            builder.addMethod(startForResultBuilder.build());
            builder.addMethod(startResultExtrasBuilder.build());
            builder.addMethod(startActivityExtrasBuilder.build());
            builder.addMethod(flagBuilder.build());
            builder.addMethod(setActionBuilder.build());
        }
        builder.addMethod(bundle);
        builder.addMethod(setExtrasBuilder.build());
        TypeSpec builderInnerClass = builder.addMethod(constructorBuilder.build()).build();

        JavaFile file = JavaFile.builder("com.shaishavgandhi.navigator", builderInnerClass).build();
        files.add(file);
        navigator.addMethod(prepareMethodBuilder.build());
    }

    private MethodSpec.Builder setFlagsMethod(ClassName builderClass) {
        return MethodSpec.methodBuilder("setFlags")
                // Add Javadoc
                .addJavadoc(CodeBlock.builder()
                        .add("Set intent flags.\n")
                        .add(("For the correct flag values see: {@link android.content.Intent}\n"))
                        .add("\n")
                        .add("@param $L The desired flags.\n", "flags ")
                        .add("@return Returns the same Builder object for chaining multiple calls\n")
                        .build())
                .addParameter(ParameterSpec.builder(TypeName.INT, "flags", Modifier.FINAL).build())
                .addModifiers(Modifier.PUBLIC)
                .returns(builderClass)
                .addStatement("this.$1L = $1L", FLAGS)
                .addStatement("return this");
    }

    private MethodSpec.Builder setActionMethod(ClassName builderClass) {
        return MethodSpec.methodBuilder("setAction")
                // Add Javadoc
                .addJavadoc(CodeBlock.builder()
                        .add("Set intent action.\n")
                        .add(("For example: {@link android.content.Intent.ACTION_VIEW}\n"))
                        .add("\n")
                        .add("@param $L The desired action.\n", "action ")
                        .add("@return Returns the same Builder object for chaining multiple calls\n")
                        .build())
                .addParameter(ParameterSpec.builder(STRING_CLASS, "action", Modifier.FINAL).build())
                .addModifiers(Modifier.PUBLIC)
                .returns(builderClass)
                .addStatement("this.$1L = $1L", ACTION)
                .addStatement("return this");
    }

    private MethodSpec.Builder getExtrasSetterMethod(ClassName builderClass) {
        return MethodSpec.methodBuilder("setExtras")
                .addModifiers(Modifier.PUBLIC)
                .returns(builderClass)
                // Add JavaDoc
                .addJavadoc(CodeBlock.builder()
                        .add("Setter for adding a {@link android.os.Bundle} to the existing bundle \n")
                        .add("that will be sent along with the {@link android.content.Intent}.\n")
                        .add("\n")
                        .add("Effectively serves as an overload for {@link android.content.Intent#putExtras\n")
                        .add("\n")
                        .add("@see #getBundle")
                        .add("\n")
                        .add("@param $L that will be appended to the current bundle\n", "extras")
                        .add("@return Builder class for chaining other methods\n")
                        .build())
                .addParameter(ParameterSpec.builder(BUNDLE_CLASSNAME, "extras")
                .addModifiers(Modifier.FINAL).build())
                .addStatement("this.$L = $L", "extras", "extras")
                .addStatement("return this");
    }

    private MethodSpec.Builder getExtrasBundle() {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("getBundle")
                .addModifiers(Modifier.PUBLIC);
        // Add javadoc
        methodBuilder.addJavadoc(CodeBlock.builder()
                .add("Returns a {@link android.os.Bundle} built from all extras that have been " +
                                "set \n" +
                        "using {@linkplain Navigator}'s prepare method.\n")
                .add("\n")
                .add("Used internally to simply get the {@link android.os.Bundle} that will be \n" +
                        "sent along with the {@link android.content.Intent}.\n")
                .add("\n")
                .add("Exposed publicly to allow custom usage of the {@link android.os.Bundle}. \n")
                .add("\n")
                .add("Example: It can be useful while navigating to a {@link android.support.v4.app.Fragment}\n" +
                        "to use {@linkplain Navigator}'s prepare method to \n" +
                        "build your bundle and call this method to get extras that can be set as \n" +
                        "arguments to your {@linkplain android.support.v4.app.Fragment}.")
                .add("\n")
                .build());
        // Add code body
        methodBuilder.addStatement("$T bundle = new $T()", BUNDLE_CLASSNAME,
                BUNDLE_CLASSNAME);
        methodBuilder.returns(BUNDLE_CLASSNAME);
        return methodBuilder;
    }

    private MethodSpec.Builder getStartActivityMethod(ClassName activity, MethodSpec bundle) {
        final String parameterName = "context";
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("start")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(CONTEXT_CLASSNAME, parameterName);
        methodBuilder.addStatement("$T intent = new $T($L, $T.class)", INTENT_CLASSNAME,
                INTENT_CLASSNAME, parameterName, activity);
        // Add javadoc
        methodBuilder.addJavadoc(CodeBlock.builder()
                .add("Terminating method in the builder. Passes the built bundle, \n")
                .add("sets any {@link android.content.Intent} flags if any and starts the \n")
                .add("activity\n")
                .add("\n")
                .add("@see #getBundle")
                .add("\n")
                .add("@param $L\n", parameterName)
                .build());
        // Put extras
        methodBuilder.addStatement("intent.putExtras($N())", bundle);

        // Set flags if they exist
        addOptionalAttributes(methodBuilder);

        // Start activity
        methodBuilder.addStatement("$L.startActivity($L)", parameterName, "intent");
        return methodBuilder;
    }

    private MethodSpec.Builder getStartActivityWithExtras(String activityName, MethodSpec bundle) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("startWithExtras")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(CONTEXT_CLASSNAME, "context")
                .addParameter(BUNDLE_CLASSNAME, "extras");
        methodBuilder.addStatement("$T intent = new $T($L, $L)", INTENT_CLASSNAME,
                INTENT_CLASSNAME, "context", activityName + ".class");

        // Add Javadoc
        methodBuilder.addJavadoc(CodeBlock.builder()
                .add("Terminating method in builder. Passes the built bundle, \n")
                .add("sets any {@link android.content.Intent} flags if any and additionally \n")
                .add("starts the activity with the provided {@link android.os.Bundle bundle}.\n")
                .add("\n")
                .add("Example: When using Shared Element Transition or any kind of Activity \n")
                .add("transition, you can use this method to pass the {@link android.os.Bundle} \n")
                .add("created by {@link android.app.ActivityOptions}.\n")
                .add("\n")
                .add("@see #getBundle")
                .add("\n")
                .add("@param $L\n", "context")
                .add("@param $L\n", "extras")
                .build());
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

        // Add JavaDoc
        methodBuilder.addJavadoc(CodeBlock.builder()
                .add("Terminating method in builder. Passes the built bundle, sets any \n")
                .add("{@link android.content.Intent} flags if any and starts the activity with \n")
                .add("the provided requestCode.\n")
                .add("\n")
                .add("@param $L\n", "activity")
                .add("@param $L\n", "requestCode")
                .build());

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

        // Add JavaDoc
        methodBuilder.addJavadoc(CodeBlock.builder()
                .add("Terminating method in builder. Passes the built bundle, sets any \n")
                .add("{@link android.content.Intent} flags if any and starts the activity with \n")
                .add("the provided requestCode and {@link android.os.Bundle extras}.\n")
                .add("\n")
                .add("@param $L\n", "activity")
                .add("@param $L\n", "requestCode")
                .build());

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

        builder.beginControlFlow("if ($L != null)", ACTION);
        builder.addStatement("$L.setAction($L)", "intent", ACTION);
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

    @NonNull private AnnotationSpec getNullabilityFor(Element element) {
        // Check both Jetbrains and Android nullable annotations since
        // Kotlin nulls are annotated with Jetbrains @Nullable
        if (element.getAnnotation(Nullable.class) == null
                && element.getAnnotation(org.jetbrains.annotations.Nullable.class) == null
                && element.getAnnotation(Optional.class) == null) {
            return AnnotationSpec.builder(ClassName.get(NonNull.class)).build();
        } else {
            return AnnotationSpec.builder(ClassName.get(Nullable.class)).build();
        }
    }

    private String getExtraTypeName(TypeMirror typeMirror) {
        String result = typeMapper.get(typeMirror.toString());
        if (result == null) {
            if (isParcelable(typeUtils, elementUtils, typeMirror)) {
                result = "Parcelable";
            } else if (isParcelableList(typeUtils, elementUtils, typeMirror)) {
                result = "ParcelableArrayList";
            } else if (isSparseParcelableArrayList(typeUtils, elementUtils, typeMirror)) {
                result = "SparseParcelableArray";
            } else if (isParcelableArray(typeUtils, elementUtils, typeMirror)) {
                result = "ParcelableArray";
            }
        }
        return result;
    }

    private boolean isParcelable(Types typeUtils, Elements elementUtils, TypeMirror typeMirror) {
        return typeUtils.isAssignable(typeMirror, elementUtils.getTypeElement("android.os.Parcelable")
                .asType());
    }

    private boolean isParcelableArray(Types typeUtils, Elements elementUtils, TypeMirror typeMirror) {
        return typeUtils.isAssignable(typeMirror, typeUtils.getArrayType(elementUtils
                .getTypeElement("android.os.Parcelable").asType()));
    }

    private boolean isParcelableList(Types typeUtils, Elements elementUtils, TypeMirror typeMirror) {
        DeclaredType type = typeUtils.getDeclaredType(elementUtils.getTypeElement("java.util" +
                        ".ArrayList"), elementUtils.getTypeElement("android.os.Parcelable").asType());
        if (typeUtils.isAssignable(typeUtils.erasure(typeMirror), type)) {
            List<? extends TypeMirror> typeArguments = ((DeclaredType) typeMirror).getTypeArguments();
            return typeArguments != null && typeArguments.size() >= 1 &&
                    typeUtils.isAssignable(typeArguments.get(0), elementUtils.getTypeElement
                            ("android.os.Parcelable").asType());
        }
        return false;
    }

    private boolean isSparseParcelableArrayList(Types typeUtils, Elements elementUtils, TypeMirror typeMirror) {
        DeclaredType type = typeUtils.getDeclaredType(elementUtils.getTypeElement("android.util.SparseArray"),
                elementUtils.getTypeElement("android.os.Parcelable").asType());
        if (typeUtils.isAssignable(typeUtils.erasure(typeMirror), type)) {
            List<? extends TypeMirror> typeArguments = ((DeclaredType) typeMirror).getTypeArguments();
            return typeArguments != null && typeArguments.size() >= 1 &&
                    typeUtils.isAssignable(typeArguments.get(0), elementUtils.getTypeElement
                            ("android.os.Parcelable").asType());
        }
        return false;
    }

    private boolean isSerializable(Types typeUtils, Elements elementUtils, TypeMirror typeMirror) {
        return typeUtils.isAssignable(typeMirror, elementUtils.getTypeElement("java.io.Serializable")
                .asType());
    }

    String getVariableKey(Element element) {
        if (element.getAnnotation(Extra.class).key().isEmpty()) {
            return element.getSimpleName().toString();
        } else {
            return element.getAnnotation(Extra.class).key();
        }
    }

    protected List<JavaFile> getFiles() {
        return files;
    }

}
