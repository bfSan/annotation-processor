package com.example.processor;

import com.google.auto.service.AutoService;
import com.sun.source.util.Trees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author bfsan
 * @since 2020/4/22
 **/
@SupportedAnnotationTypes(value = {"com.example.processor.AutoProvided"})
@SupportedSourceVersion(value = SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class MyProcessor extends AbstractProcessor {

    private TreeMaker treeMaker;
    private Name.Table names;
    private Context context;
    private Trees trees;
    private Messager messager;

    private String supplierName;

    public void init() {
        messager = processingEnv.getMessager();
        trees = Trees.instance(processingEnv);
        context = ((JavacProcessingEnvironment) processingEnv).getContext();
        treeMaker = TreeMaker.instance(context);
        names = Names.instance(context).table;

        String supplierName = null;
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("supplierConfig.properties")) {
            Properties config = new Properties();
            config.load(in);
            supplierName = config.getProperty("supplierName");
            if (supplierName == null || supplierName.length() == 0) {
                throw new IllegalArgumentException("supplierName配置未提供");
            }
        } catch (Exception e) {
            printError(e);
        }

        this.supplierName = supplierName;

        printInfo("初始化完成");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return true;
        }
        init();
        printInfo("开始处理AutoProvided");
        for (Element root : roundEnv.getRootElements()) {
            //收集标记注解的element & construct
            ElementCollector elementCollector = new ElementCollector().init();
            elementCollector.scan(root);

            printInfo("生成赋值语句");
            Collection<JCTree.JCStatement> assignmentStatements = elementCollector.getInjectableVariable()
                    .stream()
                    .map((variableElement) -> {
                        JCTree.JCExpression lExpr = treeMaker.Ident(getNameFromString(variableElement.getSimpleName().toString()));
                        JCTree.JCExpression rExpr = treeMaker.Apply(
                                null,
                                memberAccess(supplierName),
                                List.of(memberAccess(variableElement.asType() + ".class"))
                        );
                        return makeAssignment(lExpr, rExpr);
                    })
                    .collect(Collectors.toList());

            printInfo("为构造方法增加赋值语句");
            elementCollector.getConstructs()
                    .parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(construct -> {
                        JCTree.JCMethodDecl methodDecl = (JCTree.JCMethodDecl) trees.getTree(construct);

                        //将赋值语句加入到构造方法中
                        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
                        statements.addAll(methodDecl.getBody().getStatements());
                        statements.addAll(assignmentStatements);
                        methodDecl.body = treeMaker.at(methodDecl.getPreferredPosition()).Block(0, statements.toList());
                    });
        }
        printInfo("处理完成");
        return false;
    }

    private Name getNameFromString(String s) {
        return names.fromString(s);
    }

    private JCTree.JCExpression memberAccess(String components) {
        String[] componentArray = components.split("\\.");
        JCTree.JCExpression expr = treeMaker.Ident(getNameFromString(componentArray[0]));
        for (int i = 1; i < componentArray.length; i++) {
            expr = treeMaker.Select(expr, getNameFromString(componentArray[i]));
        }
        return expr;
    }


    private JCTree.JCExpressionStatement makeAssignment(JCTree.JCExpression lhs, JCTree.JCExpression rhs) {
        return treeMaker.Exec(
                treeMaker.Assign(lhs, rhs)
        );
    }

    private String getExceptionStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    private void printInfo(String msg) {
        messager.printMessage(Diagnostic.Kind.NOTE, msg);
    }

    private void printError(String msg) {
        messager.printMessage(Diagnostic.Kind.ERROR, msg);
    }

    private void printError(Exception e) {
        printError(getExceptionStackTrace(e));
    }
}
