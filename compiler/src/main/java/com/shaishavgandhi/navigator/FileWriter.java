package com.shaishavgandhi.navigator;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
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
    private static final ClassName FRAGMENT_CLASSNAME = ClassName.get("androidx.fragment.app", "Fragment");
    private static final ClassName STRING_CLASS = ClassName.bestGuess("java.lang.String");

    private static final String FLAGS = "flags";
    private static final String ACTION = "action";

    private List<ParameterSpec> constructorParams = new ArrayList<>();
    private List<ClassBuilder> classBuilders = new ArrayList<>();

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
        put("java.lang.Byte", "Byte");
        put("byte[]", "ByteArray");
        put("short", "Short");
        put("java.lang.Short", "Short");
        put("short[]", "ShortArray");
        put("char", "Char");
        put("java.lang.Character", "Char");
        put("char[]", "CharArray");
        put("java.lang.CharSequence", "CharSequence");
        put("java.lang.CharSequence[]", "CharSequenceArray");
        put("java.util.ArrayList<java.lang.CharSequence>", "CharSequenceArrayList");
        put("android.util.Size", "Size");
        put("android.util.SizeF", "SizeF");
        put("boolean", "Boolean");
        put("boolean[]", "BooleanArray");
        put("java.lang.Boolean", "Boolean");
        put("android.os.Parcelable", "Parcelable");
        put("android.os.Parcelable[]", "ParcelableArray");
        put("java.util.ArrayList<android.os.Parcelable>", "ParcelableArrayList");
    }};

    private LinkedHashMap<QualifiedClassName, LinkedHashSet<Element>> annotationsPerClass;
    private Types typeUtils;
    private Elements elementUtils;
    private List<JavaFile> files = new ArrayList<>();
    private Messager messager;

    FileWriter(Types typeUtils, Elements elementUtils, LinkedHashMap<QualifiedClassName,
            LinkedHashSet<Element>> annotationsPerClass, Messager messager) {
        this.typeUtils = typeUtils;
        this.elementUtils = elementUtils;
        this.annotationsPerClass = annotationsPerClass;
        this.messager = messager;
    }

    protected void writeFiles() {
        for (Map.Entry<QualifiedClassName, LinkedHashSet<Element>> item : annotationsPerClass.entrySet()) {
            ClassName className = item.getKey().getJavaClass();
            LinkedHashSet<Element> annotations = item.getValue();

            writeBinder(item.getKey(), annotations);
            writeBuilder(item.getKey(), annotations);
        }
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
            String varKey = getQualifiedExtraFieldName(activity, element);
            String varName = element.getSimpleName().toString();
            builder.beginControlFlow("if ($L.containsKey($L))", "bundle", varKey);

            String extraName = getExtraTypeName(element.asType());
            if (extraName == null) {
                if (isSerializable(typeUtils, elementUtils, element.asType())) {
                    // Add casting for serializable
                    builder.addStatement("$T $L = ($T) bundle.getSerializable($L)", name,
                            varName, name, varKey);
                } else {
                    messager.printMessage(Diagnostic.Kind.ERROR, element.getSimpleName().toString() + " cannot be put in Bundle");
                }
            } else {
                if (extraName.equals("ParcelableArray") || extraName.equals("Serializable")) {
                    // Add extra casting. TODO: Refactor this to be more generic
                    builder.addStatement("$T $L = ($T) bundle.get" + extraName + "($L)", name,
                            varName, name, varKey);
                } else {
                    builder.addStatement("$T $L = bundle.get" + extraName + "($L)", name, varName,
                            varKey);
                }
            }

            if (modifiers.contains(Modifier.PRIVATE)) {
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

    /**
     * Returns whether the given class is a Fragment or not.
     *
     * @param className Class of element annotated with {@link Extra}
     * @return boolean
     */
    private boolean isFragment(ClassName className) {
        TypeMirror currentClass = elementUtils.getTypeElement(className.toString()).asType();
        boolean isFragment = false;
        if (elementUtils.getTypeElement("android.support.v4.app.Fragment") != null) {
            TypeMirror supportFragment = elementUtils.getTypeElement("android.support.v4.app.Fragment").asType();
            isFragment = typeUtils.isSubtype(currentClass, supportFragment);
        }
        if (elementUtils.getTypeElement("android.app.Fragment") != null && !isFragment) {
            TypeMirror fragment = elementUtils.getTypeElement("android.app.Fragment").asType();
            isFragment = typeUtils.isSubtype(currentClass, fragment);
        }
        if (elementUtils.getTypeElement("androidx.fragment.app.Fragment") != null && !isFragment) {
            TypeMirror fragment = elementUtils.getTypeElement("androidx.fragment.app.Fragment").asType();
            isFragment = typeUtils.isSubtype(currentClass, fragment);
        }
        return isFragment;
    }

    /**
     * Returns whether the given class is an Activity or not.
     *
     * @param className Class of element annotated with {@link Extra}
     * @return boolean
     */
    private boolean isActivity(ClassName className) {
        if (elementUtils.getTypeElement("android.app.Activity") != null) {
            TypeMirror activity = elementUtils.getTypeElement("android.app.Activity").asType();
            TypeMirror currentClass = elementUtils.getTypeElement(className.toString()).asType();
            return typeUtils.isSubtype(currentClass, activity);
        }
        return false;
    }

    private void writeBinder(QualifiedClassName qualifiedClassName, LinkedHashSet<Element> annotations) {
        ClassName className = qualifiedClassName.getJavaClass();
        ClassName binderClass = getBinderClass(className);

        TypeSpec.Builder binder = TypeSpec.classBuilder(binderClass.simpleName())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        MethodSpec bindMethod = getBindMethod(className, annotations);
        binder.addMethod(bindMethod);

        TypeSpec binderResolved = binder.build();
        files.add(JavaFile.builder(className.packageName(), binderResolved).build());
    }

    /**
     * Get the `Binder` class of the given Activity/Fragment which is responsible
     * for the binding logic of the `Bundle` to the `@Extra` . For example:
     * MainActivity -> MainActivityBinder
     *
     * @param className Class of element annotated with {@link Extra}
     * @return binder class
     */
    private ClassName getBinderClass(ClassName className) {
        return ClassName.bestGuess(className.packageName() + "." + className.simpleName() +
                "Binder");
    }

    /**
     * Get the `Builder` class of the given Activity/Fragment which is responsible
     * for the builder logic and starting of the Activity. Given class `MainActivity`,
     * builder would be `MainActivityBuilder`.
     *
     * @param className Class of element annotated with {@link Extra}
     * @return builder class
     */
    private ClassName getBuilderClass(ClassName className) {
        return ClassName.bestGuess(className.packageName() + "." + className.simpleName() +
                "Builder");
    }

    private void writeBuilder(QualifiedClassName qualifiedClassName, LinkedHashSet<Element> elements) {
        ClassName activity = qualifiedClassName.getJavaClass();
        String activityName = activity.simpleName();
        TypeSpec.Builder builder = TypeSpec.classBuilder(activityName + "Builder")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        if (isActivity(activity)) {
            builder.addField(FieldSpec.builder(TypeName.INT, FLAGS)
                    .addModifiers(Modifier.PRIVATE)
                    .initializer("$L", -1)
                    .build());

            builder.addField(FieldSpec.builder(STRING_CLASS, ACTION)
                    .addModifiers(Modifier.PRIVATE)
                    .build());
        }

        builder.addField(FieldSpec.builder(BUNDLE_CLASSNAME, "extras")
                .addModifiers(Modifier.PRIVATE).build());

        ClassName builderClass = getBuilderClass(activity);

        // Constructor
        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE);

        // Set intent flags
        MethodSpec setFlagsMethod = setFlagsMethod(builderClass);

        // Set action
        MethodSpec setActionMethod = setActionMethod(builderClass);

        // Static method to prepare activity
        MethodSpec.Builder prepareMethodBuilder = getPrepareActivityMethod(builderClass);

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
            List<AnnotationSpec> annotations = getAnnotationsForElement(element);
            annotations.add(nullability);
            builder.addField(FieldSpec.builder(TypeName.get(typeMirror), name, Modifier.PRIVATE)
                    .addAnnotations(annotations)
                    .build());

            // Add the extra name as public static variable
            addKeyToClass(element, builder);

            // Add to constructor
            ParameterSpec parameter = getParameter(element, annotations);

            AnnotationSpec nullable = AnnotationSpec.builder(ClassName.get(Nullable.class)).build();
            if (!parameter.annotations.contains(nullable)) {
                constructorBuilder.addParameter(parameter);
                constructorBuilder.addStatement("this.$L = $L", parameter.name, parameter.name);

                // Add to static prepare method
                prepareMethodBuilder.addParameter(parameter);
                constructorParams.add(parameter);

                // Append to return statement
                returnStatement.append(parameter.name);
                returnStatement.append(", ");
            } else {
                addFieldToBuilder(builder, element, builderClass, annotations);
            }

            String extraName = getExtraTypeName(element.asType());

            if (extraName == null) {
                // Put to bundle
                bundleBuilder.addStatement("bundle.putSerializable($L, $L)", getExtraFieldName(element),
                        parameter.name);
            } else {
                // Put to bundle
                bundleBuilder.addStatement("bundle.put" + extraName + "($L, $L)", getExtraFieldName(element),
                        parameter.name);
            }
        }

        classBuilders.add(new ClassBuilder(qualifiedClassName, constructorParams));
        constructorParams = new ArrayList<>();

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

        // Destination intent
        MethodSpec destinationIntentMethod = getDestinationIntentMethod(activityName, bundle);

        // Start activity
        MethodSpec startActivityMethod = getStartActivityMethod(destinationIntentMethod, CONTEXT_CLASSNAME);

        // Start activity from fragment
        MethodSpec startActivityFromFragmentMethod = getStartActivityMethod(destinationIntentMethod, FRAGMENT_CLASSNAME);

        // Start activity with extras
        MethodSpec startActivityExtrasMethod = getStartActivityWithExtras(destinationIntentMethod, CONTEXT_CLASSNAME);

        // Start activity from fragment with extras
        MethodSpec startActivityFromFragmentExtrasMethod = getStartActivityWithExtras(destinationIntentMethod, FRAGMENT_CLASSNAME);

        // Start for result
        MethodSpec startForResultMethod = getStartForResultMethod(destinationIntentMethod, ACTIVITY_CLASSNAME);

        // Start from fragment with result
        MethodSpec startForResultFromFragmentMethod = getStartForResultMethod(destinationIntentMethod, FRAGMENT_CLASSNAME);

        // Start result with extras
        MethodSpec startResultExtrasMethod = getStartForResultWithExtras(destinationIntentMethod, ACTIVITY_CLASSNAME);

        // Start result from fragment with extras
        MethodSpec startResultFromFragmentExtrasMethod = getStartForResultWithExtras(destinationIntentMethod, FRAGMENT_CLASSNAME);

        MethodSpec setExtrasMethod = getExtrasSetterMethod(builderClass);

        builder.addMethod(prepareMethodBuilder.build());
        if (isActivity(activity)) {
            // Add activity specific methods
            builder.addMethod(startActivityMethod);
            builder.addMethod(startActivityFromFragmentMethod);
            builder.addMethod(startForResultMethod);
            builder.addMethod(startForResultFromFragmentMethod);
            builder.addMethod(destinationIntentMethod);
            builder.addMethod(startResultExtrasMethod);
            builder.addMethod(startResultFromFragmentExtrasMethod);
            builder.addMethod(startActivityExtrasMethod);
            builder.addMethod(startActivityFromFragmentExtrasMethod);
            builder.addMethod(setFlagsMethod);
            builder.addMethod(setActionMethod);
        }
        builder.addMethod(bundle);
        builder.addMethod(setExtrasMethod);
        TypeSpec builderInnerClass = builder.addMethod(constructorBuilder.build()).build();

        JavaFile file = JavaFile.builder(activity.packageName(), builderInnerClass).build();
        files.add(file);
    }

    /**
     * Add static final key that is used to bind each extra
     * from the bundle.
     *
     * @param element to be added
     * @param builder `Builder` class
     */
    private void addKeyToClass(Element element, TypeSpec.Builder builder) {
        NParameter extraName = getVariableKey(element);

        if (!extraName.getCustomKey()) {
            builder.addField(FieldSpec.builder(STRING_CLASS, getExtraFieldName(extraName),
                    Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("\"$L\"", extraName.getName())
                    .build());
        }
    }

    private String getExtraFieldName(Element element) {
        return getExtraFieldName(getVariableKey(element));
    }

    private String getQualifiedExtraFieldName(ClassName bindingClass, Element element) {
        NParameter param = getVariableKey(element);
        if (!param.getCustomKey()) {
            return bindingClass.simpleName() + "Builder." + getExtraFieldName(getVariableKey(element));
        }
        return getExtraFieldName(param);
    }

    private String getExtraFieldName(NParameter parameter) {
        StringBuilder builder = new StringBuilder("EXTRA");
        if (!parameter.getCustomKey()) {
            for (String word : splitByCasing(parameter.getName())) {
                builder.append("_");
                builder.append(word.toUpperCase());
            }
            return builder.toString();
        } else {
            return "\""+ parameter.getName() +"\"";
        }
    }

    private String[] splitByCasing(String variable) {
        return variable.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
    }

    /**
     * Returns a generated `setFlags` method that can be added to the
     * `Builder` class. The method sets the given flags to the Intent
     * that is used to start an Activity
     *
     * @param builderClass `Builder` class
     * @return `setFlags` method
     */
    private MethodSpec setFlagsMethod(ClassName builderClass) {
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
                .addAnnotation(CheckResult.class)
                .returns(builderClass)
                .addStatement("this.$1L = $1L", FLAGS)
                .addStatement("return this").build();
    }

    /**
     * Returns generated method `setAction` that is added to the
     * `Builder` class. Sets the given action to the Intent
     * while starting the activity.
     *
     * @param builderClass `Builder` class
     * @return `setAction` method
     */
    private MethodSpec setActionMethod(ClassName builderClass) {
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
                .addAnnotation(CheckResult.class)
                .returns(builderClass)
                .addStatement("this.$1L = $1L", ACTION)
                .addStatement("return this").build();
    }

    /**
     * Returns generated code for `setExtras` method that is added to
     * the `Builder`. This appends the supplied `Bundle` with any
     * extras that are added while starting the Activity.
     *
     * @param builderClass `Builder` class
     * @return `setExtras` method
     */
    private MethodSpec getExtrasSetterMethod(ClassName builderClass) {
        return MethodSpec.methodBuilder("setExtras")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(CheckResult.class)
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
                        .addAnnotation(Nullable.class)
                        .addModifiers(Modifier.FINAL).build()
                )
                .addStatement("this.$L = $L", "extras", "extras")
                .addStatement("return this").build();
    }

    /**
     * Returns a partially generated `getBundle` method which is the
     * central cog to the `Builder` class. All extras are added to
     * the bundle in later part of the processing.
     *
     * @return partially generated `getExtras` method
     */
    private MethodSpec.Builder getExtrasBundle() {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("getBundle")
                .addAnnotation(NonNull.class)
                .addAnnotation(CheckResult.class)
                .addModifiers(Modifier.PUBLIC);
        // Add javadoc
        methodBuilder.addJavadoc(CodeBlock.builder()
                .add("Returns a {@link android.os.Bundle} built from all extras that have been " +
                                "set \n" +
                        "using the builder methods.\n")
                .add("\n")
                .add("Used internally to simply get the {@link android.os.Bundle} that will be \n" +
                        "sent along with the {@link android.content.Intent}.\n")
                .add("\n")
                .add("Exposed publicly to allow custom usage of the {@link android.os.Bundle}. \n")
                .add("\n")
                .add("Example: It can be useful while navigating to a {@link androidx.fragment.app.Fragment}\n" +
                        "to use the builder methods to \n" +
                        "construct your bundle and call this method to get extras that can be set" +
                        " as \n" +
                        "arguments to your {@linkplain androidx.fragment.app.Fragment}.")
                .add("\n")
                .build());
        // Add code body
        methodBuilder.addStatement("$T bundle = new $T()", BUNDLE_CLASSNAME,
                BUNDLE_CLASSNAME);
        methodBuilder.returns(BUNDLE_CLASSNAME);
        return methodBuilder;
    }

    /**
     * Returns the generated `startActivity` method which is added to the
     * `Builder` class.
     *
     * @param destinationIntent Intent to be started.
     * @return `startActivity` method
     */
    private MethodSpec getStartActivityMethod(MethodSpec destinationIntent, ClassName originClass) {
        final String parameterName = originClass.simpleName().toLowerCase();
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("start")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(originClass, parameterName)
                        .addAnnotation(NonNull.class).build()
                );
        String context = resolveContext(originClass);
        methodBuilder.addStatement("$T intent = $N($L)", INTENT_CLASSNAME,
                destinationIntent, context);
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

        // Start activity
        methodBuilder.addStatement("$L.startActivity($L)", parameterName, "intent");
        return methodBuilder.build();
    }

    private String resolveContext(ClassName className) {
        if (className.simpleName().equals("Fragment")) {
            return className.simpleName().toLowerCase() + ".getActivity()";
        }
        return className.simpleName().toLowerCase();
    }

    /**
     * Generates the `startActivity` method with an overload for a `Bundle`
     * which is added to the `Builder` class.
     *
     * @param destinationIntent the Intent to be started.
     * @return `startActivity` method.
     */
    private MethodSpec getStartActivityWithExtras(MethodSpec destinationIntent, ClassName originClass) {
        final String parameterName = originClass.simpleName().toLowerCase();
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("startWithExtras")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(originClass, parameterName)
                        .addAnnotation(NonNull.class)
                        .build()
                )
                .addParameter(BUNDLE_CLASSNAME, "extras");
        String context = resolveContext(originClass);
        methodBuilder.addStatement("$T intent = $N($L)", INTENT_CLASSNAME,
                destinationIntent, context);

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
                .add("@param $L\n", parameterName)
                .add("@param $L\n", "extras")
                .build());

        // Start activity
        methodBuilder.addStatement("$L.startActivity($L, $L)", parameterName, "intent", "extras");
        return methodBuilder.build();
    }

    /**
     * Adds a setter to the `Builder` class for the given element.
     * Example: `setUserId(long id)`
     *
     * @param builder `Builder` class
     * @param element to be added to the `Builder` as a setter.
     * @param builderClass {@link ClassName} representation of `Builder` which is returned
     *                                      to have a fluent API.
     */
    private void addFieldToBuilder(TypeSpec.Builder builder, Element element, ClassName builderClass, List<AnnotationSpec> annotations) {
        String variableName = element.getSimpleName().toString();
        MethodSpec.Builder setter = MethodSpec.methodBuilder("set" + capitalize(variableName))
                .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(TypeName.get(element.asType()), variableName)
                        .addAnnotations(annotations)
                        .build())
                .addAnnotation(CheckResult.class)
                .addStatement("this.$L = $L", variableName, variableName)
                .returns(builderClass)
                .addStatement("return this");

        builder.addMethod(setter.build());
    }

    /**
     * Generates the `startForResult` method which is added to
     * the `Builder` class.
     *
     * @param destinationIntent the intent to start the Activity.
     * @return `startForResult` method.
     */
    private MethodSpec getStartForResultMethod(MethodSpec destinationIntent, ClassName originClass) {
        final String parameterName = originClass.simpleName().toLowerCase();
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("startForResult")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(originClass, parameterName)
                        .addAnnotation(NonNull.class)
                        .build())
                .addParameter(TypeName.INT, "requestCode");
        String context = resolveContext(originClass);
        methodBuilder.addStatement("$T intent = $N($L)", INTENT_CLASSNAME,
                destinationIntent, context);

        // Add JavaDoc
        methodBuilder.addJavadoc(CodeBlock.builder()
                .add("Terminating method in builder. Passes the built bundle, sets any \n")
                .add("{@link android.content.Intent} flags if any and starts the activity with \n")
                .add("the provided requestCode.\n")
                .add("\n")
                .add("@param $L\n", parameterName)
                .add("@param $L\n", "requestCode")
                .build());
        // Start for result
        methodBuilder.addStatement("$L.startActivityForResult($L, $L)", parameterName,
                "intent", "requestCode");
        return methodBuilder.build();
    }

    private MethodSpec getDestinationIntentMethod(String activityName, MethodSpec bundle) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("getDestinationIntent")
                .addModifiers(Modifier.PRIVATE)
                .addAnnotation(NonNull.class)
                .addParameter(ParameterSpec.builder(CONTEXT_CLASSNAME, "context")
                        .addAnnotation(NonNull.class)
                        .build()
                ).returns(INTENT_CLASSNAME);

        methodBuilder.addStatement("$T intent = new $T($L, $L)", INTENT_CLASSNAME,
                INTENT_CLASSNAME, "context", activityName + ".class");

        methodBuilder.addJavadoc(CodeBlock.builder()
                .add("Returns the {@link android.content.Intent} that will be used to start the Activity.\n")
                .add("\n")
                .add("Sets optional fields like {@link flags}, {@link action} if they are supplied by\n")
                .add("you in the builder methods.\n")
                .add("\n")
                .add("@param $L the context used in Intent.\n", "context")
                .add("@return the constructed Intent\n")
                .build());

        // Put extras
        methodBuilder.addStatement("intent.putExtras($N())", bundle);
        //Add flags if any
        addOptionalAttributes(methodBuilder);
        methodBuilder.addStatement("return intent");
        return methodBuilder.build();
    }

    /**
     * Generates the `startForResult` method with overload for a `Bundle`. This
     * is added to the `Builder` class to start an Activity with a result.
     *
     * @param destinationIntent the Intent to be started.
     * @return `startForResult` method
     */
    private MethodSpec getStartForResultWithExtras(MethodSpec destinationIntent, ClassName originClass) {
        final String parameterName = originClass.simpleName().toLowerCase();
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("startForResult")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(originClass, parameterName)
                        .addAnnotation(NonNull.class)
                        .build()
                )
                .addParameter(TypeName.INT, "requestCode")
                .addParameter(ParameterSpec.builder(BUNDLE_CLASSNAME, "extras", Modifier.FINAL)
                .addAnnotation(Nullable.class).build());
        String context = resolveContext(originClass);
        methodBuilder.addStatement("$T intent = $N($L)", INTENT_CLASSNAME,
                destinationIntent, context);

        // Add JavaDoc
        methodBuilder.addJavadoc(CodeBlock.builder()
                .add("Terminating method in builder. Passes the built bundle, sets any \n")
                .add("{@link android.content.Intent} flags if any and starts the activity with \n")
                .add("the provided requestCode and {@link android.os.Bundle extras}.\n")
                .add("\n")
                .add("@param $L\n", parameterName)
                .add("@param $L\n", "requestCode")
                .build());

        // Start activity for result
        methodBuilder.addStatement("$L.startActivityForResult($L, $L, $L)", parameterName,
                "intent", "requestCode", "extras");
        return methodBuilder.build();
    }

    /**
     * Returns a partially generated static factory method that is added to the
     * `Builder` for easy access. i.e `MainActivityBuilder.builder(args)`
     *
     * @param builderClass `Builder` returned for a chaining API
     * @return Partially generated method that is appended later.
     */
    private MethodSpec.Builder getPrepareActivityMethod(ClassName builderClass) {
        MethodSpec.Builder prepareMethodBuilder = MethodSpec.methodBuilder("builder");
        prepareMethodBuilder.addModifiers(Modifier.STATIC, Modifier.FINAL, Modifier.PUBLIC);
        prepareMethodBuilder.returns(builderClass);
        prepareMethodBuilder.addAnnotation(CheckResult.class);
        return prepareMethodBuilder;
    }

    /**
     * Adds optional attributes like `setFlags` and `setAction`
     * to the given builder.
     *
     * @param builder `Builder` class
     */
    private void addOptionalAttributes(MethodSpec.Builder builder) {
        builder.beginControlFlow("if ($L != -1)", FLAGS);
        builder.addStatement("$L.setFlags($L)", "intent", FLAGS);
        builder.endControlFlow();

        builder.beginControlFlow("if ($L != null)", ACTION);
        builder.addStatement("$L.setAction($L)", "intent", ACTION);
        builder.endControlFlow();
    }

    /**
     * Get {@link ParameterSpec} from given element.
     * Checks for nullability, types etc and returns
     * the parameter.
     *
     * @param element to be converted to Parameter
     * @return {@link ParameterSpec}
     */
    private ParameterSpec getParameter(Element element, List<AnnotationSpec> annotations) {
        TypeMirror typeMirror = element.asType();
        String name = element.getSimpleName().toString();
        ParameterSpec.Builder parameterBuilder = ParameterSpec.builder(TypeName.get(typeMirror), name);
        parameterBuilder.addModifiers(Modifier.FINAL);

        parameterBuilder.addAnnotations(annotations);

        return parameterBuilder.build();
    }

    private List<AnnotationSpec> getAnnotationsForElement(Element element) {
        List<AnnotationSpec> annotations = new ArrayList<>();

        for (AnnotationMirror annotation: element.getAnnotationMirrors()) {
            if (isAnnotationWhitelisted(annotation)) {
                continue;
            }
            annotations.add(AnnotationSpec.builder(ClassName.bestGuess(annotation.getAnnotationType().toString()))
                    .build());
        }
        return annotations;
    }

    public static boolean isAnnotationWhitelisted(AnnotationMirror annotation) {
        return annotation.getAnnotationType().toString().contains("Nullable")
                || annotation.getAnnotationType().toString().contains("NonNull")
                || annotation.getAnnotationType().toString().contains("NotNull")
                || annotation.getAnnotationType().toString().contains("Extra")
                || annotation.getAnnotationType().toString().contains("Optional");
    }

    /**
     * Returns nullability annotation for given element.
     * Checks for Jetbrains' {@link org.jetbrains.annotations.Nullable}
     * as well as Android's {@link Nullable}.
     *
     * @param element annotated with {@link Extra}
     * @return Nullability annotation
     */
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

    /**
     * Returns the extra type name for given type.
     * This method is useful in mapping from types
     * like Parcelable, ParcelableArrayList etc. to
     * the appropriate bundle type.
     *
     * @param typeMirror of the element
     * @return type
     */
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

    public static boolean isParcelable(Types typeUtils, Elements elementUtils, TypeMirror typeMirror) {
        return typeUtils.isAssignable(typeMirror, elementUtils.getTypeElement("android.os.Parcelable")
                .asType());
    }

    public static boolean isParcelableArray(Types typeUtils, Elements elementUtils, TypeMirror typeMirror) {
        return typeUtils.isAssignable(typeMirror, typeUtils.getArrayType(elementUtils
                .getTypeElement("android.os.Parcelable").asType()));
    }

    public static boolean isParcelableList(Types typeUtils, Elements elementUtils, TypeMirror typeMirror) {
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

    public static boolean isSparseParcelableArrayList(Types typeUtils, Elements elementUtils, TypeMirror typeMirror) {
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

    public static boolean isSerializable(Types typeUtils, Elements elementUtils, TypeMirror typeMirror) {
        return typeUtils.isAssignable(typeMirror, elementUtils.getTypeElement("java.io.Serializable")
                .asType());
    }


    NParameter getVariableKey(Element element) {
        if (element.getAnnotation(Extra.class).key().isEmpty()) {
            return new NParameter(element.asType(), element.getSimpleName().toString(), false);
        } else {
            return new NParameter(element.asType(), element.getAnnotation(Extra.class).key(), true);
        }
    }

    protected List<JavaFile> getFiles() {
        return files;
    }

    public List<ClassBuilder> getClassBuilders() {
        return classBuilders;
    }
}
