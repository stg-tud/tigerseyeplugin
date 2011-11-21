package de.tud.stg.popart.builder.test.dsls;
import static de.tud.stg.tigerseye.dslsupport.annotations.DSLMethod.DslMethodType.*;


import de.tud.stg.tigerseye.dslsupport.Interpreter;
import de.tud.stg.tigerseye.dslsupport.annotations.DSLMethod;
import de.tud.stg.popart.dslsupport.*;

import org.javalogo.*;
import java.awt.Color;
import javax.swing.text.MaskFormatter.LiteralCharacter;
/**
 * This class implements the Logo toy language.
 */
public class LogoDSLForTransformationTest /*extends Interpreter */{
/*
	def DEBUG = true;

	private TurtleGraphicsWindow myTurtleGraphicsWindow;
	private Turtle turtle;

	public LogoDSLForTransformationTest() {
	}

	public Object eval(HashMap map, Closure cl) {
		if (myTurtleGraphicsWindow == null) {
			myTurtleGraphicsWindow = new TurtleGraphicsWindow();
			myTurtleGraphicsWindow.setTitle("TurtleDSL (based on JavaLogo) "); //Set the windows title
			myTurtleGraphicsWindow.show(); //Display the window
			turtle = new Turtle("Noname",java.awt.Color.BLACK); //Create a turtle
			myTurtleGraphicsWindow.add(turtle); //Put bob in our window so bob has a place to draw
		}

		turtle.setName(map.name);
		cl.delegate = this;
		cl.resolveStrategy = Closure.DELEGATE_FIRST;
		cl.call();
	}

	// Literals 
	@DSLMethod(type=Literal)
	public int getBlack() {
		return Color.BLACK.value;
	}
	@DSLMethod(type=Literal)
	public int getBlue() {
		return Color.BLUE.value;
	}
	@DSLMethod(type=Literal)
	public int getRed() {
		return Color.RED.value;
	}
	@DSLMethod(type=Literal)
	public int getGreen() {
		return Color.GREEN.value;
	}
	@DSLMethod(type=Literal)
	public int getYellow() {
		return Color.YELLOW.value;
	}
	@DSLMethod(type=Literal)
	public int getWhite() {
		return Color.WHITE.value;
	}

	// Operations 
	@DSLMethod()
	public void textscreen()   {
		throw new IllegalStateException("DSL Operation has not been implemented.")
	}
	@DSLMethod()
	public void ts() {
		textscreen();
	}
	@DSLMethod()
	public void fullscreen() {
		throw new IllegalStateException("DSL Operation has not been implemented.")
	}
	@DSLMethod()
	public void fs() {
		fullscreen();
	}
	@DSLMethod()
	public void home() {
		turtle.home();
	}
	@DSLMethod()
	public void clean() {
		myTurtleGraphicsWindow.clear();
	}
	@DSLMethod()
	public void cleanscreen() {
		clean(); home();
	}
	@DSLMethod()
	public void cs() {
		cleanscreen();
	}

	@DSLMethod()
	public void hideturtle() {
		turtle.hide();
	}
	@DSLMethod()
	public void ht() {
		hideturtle();
	}
	@DSLMethod()
	public void showturtle() {
		turtle.show();
	}
	@DSLMethod()
	public void st() {
		showturtle();
	}

	@DSLMethod()
	public void setpencolor(int n) {
		turtle.setPenColor(new java.awt.Color(n));
	}
	@DSLMethod()
	public void setpc(int n) {
		setpencolor(n);
	}
	@DSLMethod()
	public void penup() {
		turtle.penUp();
	}
	@DSLMethod()
	public void pu() {
		penup();
	}
	@DSLMethod()
	public void pendown() {
		turtle.penDown();
	}
	@DSLMethod()
	public void pd() {
		pendown();
	}*/
	@DSLMethod(production="forward__p0")
	public void forward(int n) {
		turtle.forward(n);
	}
//	@DSLMethod()
//	public void fd(int n) {
//		forward(n);
//	}	@DSLMethod()
	public void backward(int n) {
		turtle.backward(n);
	}
//	@DSLMethod()
//	public void bd(int n) {
//		backward(n);
//	}//	@DSLMethod()
//	public void right(int n) {
//		turtle.right(n);
//	}
//	@DSLMethod()
//	public void rt(int n) {
//		right(n);
//	}//	@DSLMethod()
//	public void left(int n) {
//		turtle.left(n);
//	}
//	@DSLMethod()
//	public void lt(int n) {
//		left(n);
//	}

	public void methodWithoutParameter() {
	}

	public void methodWithOneParameter(int one) {
	}
	
	public void methodWithTowParameter(int one, int two) {
	}
}

class Turtle {
	def setName(name){

	}
	def home(){

	}
	def clear(){

	}
	def hide(){

	}
	def show(){

	}
	def forward(){

	}
	def backward(){

	}
	def right(){

	}
	def left(){

	}
	def penDown(){

	}
	def penUp(){

	}
	def setPenColor(color){

	}
}
class TurtleGraphicsWindow {
	def setTitle(String str){

	}
	def show(){

	}
	def add(turtle){

	}
	def clear(){

	}
}