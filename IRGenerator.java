import java.util.ArrayList;
import java.util.Stack;
public class IRGenerator {

  public Stack < IRInstruction > expr_instructions;
  public ArrayList < IRInstruction > instructions;
  public int temp_next;

  public IRGenerator() {
   
    expr_instructions = new Stack < > ();
    instructions = new ArrayList < > ();
    temp_next = 1;
  }
  
  
  public Operand allocate_temporary() {
    Operand result = Operand.temp_operand("$T" + temp_next, null);
    temp_next += 1;
    
    return result;
  }
 
  public IRInstruction add_instruction() {
    IRInstruction result = new IRInstruction();
    instructions.add(result);
    return result;
  }
  public IRInstruction push_instruction() {
    IRInstruction top = new IRInstruction();
    expr_instructions.push(top);
    return top;
  }
  public IRInstruction top_instruction() {
    if (!expr_instructions.empty()) {
      return expr_instructions.peek();
    } else {
      return null;
    }
    
  }
  public IRInstruction pop() {
    if (!expr_instructions.empty()) {
      IRInstruction top = expr_instructions.pop();
      instructions.add(top);
      return top;
    } else {
      return null;
    }
  }
}


 class Operand {

  public Type type;
  public String value;

  public enum Type 
  {
    INT_VAR,
    FLOAT_VAR,
    STRING_VAR,
    INT_LIT,
    FLOAT_LIT,
    
  }
  public static Operand symbol_operand(Symbol symbol) {
    Operand result = new Operand();
    result.value = symbol.get_name();
    if (symbol.get_type().equals("INT")) {
      result.type = Type.INT_VAR;
    } else if (symbol.get_type().equals("FLOAT")) {
      result.type = Type.FLOAT_VAR;
    } else if (symbol.get_type().equals("STRING")) {
      result.type = Type.STRING_VAR;
    }
    return result;
  }
  public static Operand temp_operand(String name, Type type)
   {
    Operand result = new Operand();
    result.value = name;
    result.type = type;
    return result;
  }
 
  @Override
  public String toString() {
    return value;
  }
  public static Operand int_lit_operand(String value) {
    Operand result = new Operand();
    result.type = Type.INT_LIT;
    result.value = value;
    return result;
  }
  public static Operand float_lit_operand(String value) {
    Operand result = new Operand();
    result.type = Type.FLOAT_LIT;
    result.value = value;
    return result;
  }
  public boolean is_int() {
    return type == Type.INT_LIT || type == Type.INT_VAR;
  }
  public boolean is_float() {
    return type == Type.FLOAT_VAR || type == Type.FLOAT_LIT;
  }
  public boolean is_lit() {
    return type == Type.INT_LIT || type == Type.FLOAT_LIT;
    
  }
  
}