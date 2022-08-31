
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import java.util.*;



public class Driver{




	

public static void main(String[] args) throws Exception {
	      @SuppressWarnings("deprecation")
        ANTLRInputStream input = new ANTLRInputStream(System.in);
        LittleLexer lexer = new  LittleLexer(input);
      CommonTokenStream tokens = new CommonTokenStream(lexer);
        LittleParser parser = new LittleParser(tokens);
       ParseTree tree = parser.program();
			 ParseTreeWalker walker = new ParseTreeWalker();
			 
			 SymbolExtractor stb = new SymbolExtractor();
			 

			walker.walk(stb,tree);

			output_IRcode (stb. ir_generator . instructions );
			TinyCodeEmitter emitter = new TinyCodeEmitter();
			String result = emitter.emitCode(stb. currentTable , stb. ir_generator . instructions );
			System. out .println(result);


      

    }

		private static void output_IRcode(List<IRInstruction> instructions)
		{
			
			System. out .println( ";IR code" );
			System. out .println( ";LABEL main" );
			System. out .println( ";LINK" );
		
			for (IRInstruction instruction : instructions)
			{
			System. out .println( ";" + instruction);
		
			}
			// Output post
			System. out .println( ";RET" );
			System. out .println( ";tiny code" );
		}
}


  
class SymbolExtractor extends LittleBaseListener {
	
	
    public SymbolTable currentTable;
		public IRGenerator ir_generator ;
		public int block_id = 1;
	
	//LinkedHashMap<String, Symbol> _symbols = new LinkedHashMap<String, Symbol>();
		
	@Override 
	public void enterProgram(LittleParser.ProgramContext ctx) { 
		currentTable = new SymbolTable(null, "GLOBAL");
		ir_generator = new IRGenerator();
		
		
	}
	
	@Override 
	public void exitProgram(LittleParser.ProgramContext ctx) {
		


  //System.out.println(currentTable);

	}
	
	
	@Override 
	public void enterDecl(LittleParser.DeclContext ctx) { 
		
	}
	
	@Override 
	public void exitDecl(LittleParser.DeclContext ctx) { 
		
	}
	
	@Override 
	public void enterString_decl(LittleParser.String_declContext ctx) { 
		
		Symbol sym = new Symbol("STRING", ctx.id().getText(), ctx.str().getText());
        currentTable.add(sym.get_name(), sym);
				
	}
	
	@Override 
	public void exitString_decl(LittleParser.String_declContext ctx) { 
		
	}
	
	@Override 
	public void enterVar_decl(LittleParser.Var_declContext ctx) { 
		String var_type = ctx.var_type().getText();
		Symbol id_sym = new Symbol(var_type, ctx.id_list().id().getText());
		currentTable.add(id_sym.get_name(), id_sym);
		LittleParser.Id_tailContext tail = ctx.id_list().id_tail();
		while (tail.getChildCount() != 0)
       {
		Symbol tail_sym = new Symbol(var_type, tail.id().getText());
		currentTable.add(tail_sym.get_name(), tail_sym);
		tail = tail.id_tail();

	   }

	}
	
	@Override 
	public void exitVar_decl(LittleParser.Var_declContext ctx) { 
		
	}
	
	@Override 
	public void enterParam_decl(LittleParser.Param_declContext ctx) { 
		Symbol sym = new Symbol(ctx.var_type().getText(), ctx.id().getText());
		currentTable.add(sym.get_name(), sym);
	}
	
	@Override
	public void exitParam_decl(LittleParser.Param_declContext ctx) { 
		
	}
	
	@Override 
	public void enterFunc_decl(LittleParser.Func_declContext ctx) { 
		currentTable = new SymbolTable(currentTable, ctx.id().getText());
	}
	
	@Override 
	public void exitFunc_decl(LittleParser.Func_declContext ctx) { 
	    currentTable = currentTable.get_parent();
	
	}
	

	@Override 
	public void enterParam_decl_list(LittleParser.Param_decl_listContext ctx) { }

	@Override 
	public void exitParam_decl_list(LittleParser.Param_decl_listContext ctx) { }




	@Override 
	public void enterIf_stmt(LittleParser.If_stmtContext ctx) { 
		currentTable= new SymbolTable(currentTable, "BLOCK " + block_id);
		block_id += 1;


		
		
	}
	@Override 
	public void exitIf_stmt(LittleParser.If_stmtContext ctx) { 

		currentTable = currentTable.get_parent();
		
	}
	@Override 
	public void enterElse_part(LittleParser.Else_partContext ctx) { 
		
		if (ctx.getChildCount() == 0){
			return;
		}

		currentTable = currentTable.get_parent();
      currentTable = new SymbolTable(currentTable, "BLOCK " + block_id);
      block_id += 1;
	}
	
	@Override 
	public void exitElse_part(LittleParser.Else_partContext ctx) { 
		
	}
	@Override 
	public void enterWhile_stmt(LittleParser.While_stmtContext ctx) { 

		currentTable = new SymbolTable(currentTable, "BLOCK " + block_id);
		block_id += 1;

	}
	
	
	@Override 
	public void exitWhile_stmt(LittleParser.While_stmtContext ctx) { 
		currentTable = currentTable.get_parent();
	}

	@Override
	public void enterRead_stmt(LittleParser.Read_stmtContext ctx)
	{
			// Get the first id
		Symbol id_sym = currentTable .find(ctx.id_list().id().getText());
		add_read_instruction(id_sym);

		// Add the remainder of the id list
		LittleParser.Id_tailContext tail = ctx.id_list().id_tail();
		while (tail.getChildCount() != 0 )
		{
				id_sym = currentTable .find(tail.id().getText());
				add_read_instruction(id_sym);
				tail = tail.id_tail();
		}
	}
	@Override
public void enterWrite_stmt(LittleParser.Write_stmtContext ctx)
	{
		// Get the first id
		Symbol id_sym = currentTable.find(ctx.id_list().id().getText());
		add_write_instruction(id_sym);
		// Add the remainder of the id list
		LittleParser.Id_tailContext tail = ctx.id_list().id_tail();
		while (tail.getChildCount() != 0 )
		{
				id_sym = currentTable .find(tail.id().getText());
				add_write_instruction(id_sym);
				tail = tail.id_tail();

		}
}
	@Override
public void enterAssign_expr(LittleParser.Assign_exprContext ctx) {
  
  Symbol value1 = currentTable.find(ctx.id().getText());
  IRInstruction assign_inst = ir_generator.push_instruction();
  assign_inst.result = Operand.symbol_operand(value1);
}


@Override
public void exitAssign_expr(LittleParser.Assign_exprContext ctx) {
  
  IRInstruction assign_inst = ir_generator.pop();
  if (assign_inst != null && assign_inst.op == null) {
    if (assign_inst.result.type == Operand.Type.INT_VAR) {
      assign_inst.op = IRInstruction.OP.STOREI;
    } else if (assign_inst.result.type == Operand.Type.FLOAT_VAR) {
      assign_inst.op = IRInstruction.OP.STOREF;
    }
  }
}

@Override
public void enterPrimary(LittleParser.PrimaryContext ctx) {
  if (ctx.id() != null) {
    Symbol sym = currentTable.find(ctx.id().getText());
    Operand operand = Operand.symbol_operand(sym);
    assign_operand(operand);
    pop_if_complete();
  } else if (ctx.INTLITERAL() != null) {
    assign_operand(Operand.int_lit_operand(ctx.INTLITERAL().getText()));
    pop_if_complete();
  } else if (ctx.FLOATLITERAL() != null) {
    assign_operand(Operand.float_lit_operand(ctx.FLOATLITERAL().getText()));
    pop_if_complete();
  }
}
@Override
public void enterAddop(LittleParser.AddopContext ctx) {
  IRInstruction instr = ir_generator.top_instruction();
  
assert(instr.operand_1.is_int() || instr.operand_1.is_float());


if (ctx.getText().equals("+")) {
  if (instr.operand_1.is_int()) {
    instr.op = IRInstruction.OP.ADDI;
  } else {
    instr.op = IRInstruction.OP.ADDF;
  }
} else {
  assert(ctx.getText().equals("-"));
  if (instr.operand_1.is_int()) {
    instr.op = IRInstruction.OP.SUBI;
  } else {
    instr.op = IRInstruction.OP.SUBF;
  }
}
}
@Override
public void enterMulop(LittleParser.MulopContext ctx) {
  IRInstruction instr = ir_generator.top_instruction();
  
assert(instr.operand_1.is_int() || instr.operand_1.is_float());

if (ctx.getText().equals("*")) {
  if (instr.operand_1.is_int()) {
    instr.op = IRInstruction.OP.MULTI;
  } else {
    instr.op = IRInstruction.OP.MULTF;
  }
} else {
  
  assert(ctx.getText().equals("/"));
  if (instr.operand_1.is_int()) {
    instr.op = IRInstruction.OP.DIVI;
  } else {
    instr.op = IRInstruction.OP.DIVF;
  }
}
}

private void determine_result_type() {
  IRInstruction instr = ir_generator.top_instruction();
  if (instr.result.type != null) {
    return;
  }
  assert(instr.operand_1.is_int() || instr.operand_1.is_float());
  if (instr.operand_1.is_int()) {
    instr.result.type = Operand.Type.INT_VAR;
  } else {
    instr.result.type = Operand.Type.FLOAT_VAR;
    
  }
}
private void assign_operand(Operand operand) {
  IRInstruction instr = ir_generator.top_instruction();
  assert(instr.operand_1 == null || instr.operand_2 == null);
  if (instr.operand_1 == null) {
    instr.operand_1 = operand;
  } else {
    instr.operand_2 = operand;
  }
}
private void pop_if_complete() {
  IRInstruction instr = ir_generator.top_instruction();
  if (instr != null && instr.operand_1 != null && instr.op != null &&
    instr.operand_2 != null) {
    determine_result_type();
    ir_generator.pop();
    pop_if_complete();
  }
}
private void add_read_instruction(Symbol symbol) {
  IRInstruction instr = ir_generator.add_instruction();
  instr.result = Operand.symbol_operand(symbol);
  if (symbol.get_type().equals("INT")) {
    instr.op = IRInstruction.OP.READI;
  } else if (symbol.get_type().equals("FLOAT")) {
    instr.op = IRInstruction.OP.READF;
  }
}
private void add_write_instruction(Symbol symbol) {
  IRInstruction instr = ir_generator.add_instruction();
  instr.result = Operand.symbol_operand(symbol);
  if (symbol.get_type().equals("INT")) {
    
    instr.op = IRInstruction.OP.WRITEI;
  } else if (symbol.get_type().equals("FLOAT")) {
    instr.op = IRInstruction.OP.WRITEF;
  } else if (symbol.get_type().equals("STRING")) {
    instr.op = IRInstruction.OP.WRITES;
  }
}
	
}





class Symbol{

	
	public Symbol(String type, String name)
	{
			_type = type;
			_name = name;
	}
	public Symbol(String type, String name, String value)
	{
			_type = type;
			_name = name;
			_value = value;
	}
	public String get_type()
	{
			return _type;
	}
	public String get_name()
	{
			return _name;
	}
	public String get_value()
	{
			return _value;
	}
	@Override
	public   String toString()
	{
			return "name " + _name + " type " + _type + (_value != null ? " value " +
			_value : "");
	}
	private String _type;
	private String _name;
	private String _value;

}




class IRInstruction {

	public OP op = null;
  public Operand operand_1 = null;
  public Operand operand_2 = null;
  public Operand result = null;
  public enum OP {
    ADDI,
    ADDF,
    SUBI,
    SUBF,
    MULTI,
    MULTF,
    DIVI,
    DIVF,
    STOREI,
    STOREF,
    READI,
    READF,
    WRITEI,
    WRITEF,
    WRITES
    
  }
  @Override
  public String toString() {
    String str = op.toString();
    if (operand_1 != null) {
      str += " " + operand_1;
    }
    if (operand_2 != null) {
      str += " " + operand_2;
    }
    return str + " " + result;

		
  }
	

 
}





