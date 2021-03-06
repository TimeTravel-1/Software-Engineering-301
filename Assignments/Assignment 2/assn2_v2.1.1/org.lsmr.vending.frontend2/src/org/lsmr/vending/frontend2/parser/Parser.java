/* Generated By:JavaCC: Do not edit this line. Parser.java */
package org.lsmr.vending.frontend2.parser;

import java.util.*;
import org.lsmr.vending.frontend2.*;
import org.lsmr.vending.frontend2.hardware.*;

@SuppressWarnings("all")
public class Parser implements ParserConstants {
  private boolean debug = false;

  public void setDebug(boolean flag) {
    debug = flag;
  }

  public Parser(String s) {
    this(new java.io.StringReader(s));
  }

  public boolean inputIsConsumed()
  {
    try
    {
      Token t = token_source.getNextToken();
      if (t.kind != ParserConstants.EOF || t.specialToken != null) return false;
    }
    catch (TokenMgrError e)
    {
      return false;
    }
    return true;
  }

  private IVendingMachineFactory vm = null;
  private ArrayList<Object> extraction = new ArrayList<Object>();
  private VendingMachineStoredContents teardown = null;

  public void register(IVendingMachineFactory vm) {
    this.vm = vm;
  }

  private boolean checkDelivery(int change, List<String> popCans)
  {
    boolean result = true;

    if(extraction == null)
      return false;

        for(Object o: extraction)
        {
          if(o instanceof Coin)
            change -= ((Coin)o).getValue();
          else
          {
            String name = ((PopCan)o).getName();
            if(!(popCans.contains(name))) {
                result = false;
                if(debug)
                  System.err.println("Failure: " + name + " has been returned where it should not have been");
        }
        else
          popCans.remove(name);
          }
        }

        if(change != 0) {
          result = false;
          if(debug)
            System.err.println("Failure: " + change + " != 0");
    }

        if(!popCans.isEmpty())
                for(String s : popCans) {
                  result = false;
                  if(debug)
                    System.err.println("Failure: expected to find " + s);
                }

        return result;
  }


    private boolean checkTeardown(int change, int payments, List<String> pops) {
        boolean result = true;

        if(teardown == null) {
            if(debug)
                System.err.println("Failure: teardown does not contain at least the two integers");
            return false;
        }

        int unusedValue = 0;
        try {
            List<List<Coin>> coins = teardown.coinsInCoinRacks;
            for(List<Coin> list : coins)
                for(Coin c : list)
                    unusedValue += c.getValue();
        }
        catch(ClassCastException cce) {
            result = false;
            if(debug)
                System.err.println("Failure: unusued change contains a non-Coin object");
        }

        int paymentValue = 0;
        try {
            List<Coin> coins = teardown.paymentCoinsInStorageBin;
            for(Coin c : coins)
                paymentValue += c.getValue();
        }
        catch(ClassCastException cce) {
            result = false;
            if(debug)
                System.err.println("Failure: payment coins contains a non-Coin object");
        }

        if(unusedValue != change) {
            result = false;
            if(debug)
                System.err.println("Failure: change expected is " + change + " but was " + unusedValue);
        }

        if(paymentValue != payments) {
            result = false;
            if(debug)
                System.err.println("Failure: payments expected is " + payments + " but was " + paymentValue);
        }

        List<List<PopCan>> unsoldPops = teardown.popCansInPopCanRacks;
        try {
            for(List<PopCan> list : unsoldPops)
                for(PopCan pop : list) {
                    String name = pop.getName();
                    if(!(pops.contains(name))) {
                          result = false;
                          if(debug)
                              System.err.println("Failure: " + name + " has been returned where it should not have been");
            }
                        else
                            pops.remove(name);
                }
        }
        catch(ClassCastException cce) {
            result = false;
            if(debug)
                System.err.println("Failure: unsold pops contains a non-Pop object");
        }

        if(!pops.isEmpty())
            for(String s : pops) {
                result = false;
                if(debug)
                    System.err.println("Failure: expected to find " + s);
            }

        return result;
    }


  private void announceConstruct(ArrayList<Integer> coinKinds, int selectionButtonCount, int coinRackCapacity, int popCanRackCapacity,
            int receptacleCapacity) {
    vm.constructNewVendingMachine(coinKinds, selectionButtonCount, coinRackCapacity, popCanRackCapacity,
            receptacleCapacity);
  }

  private void announceConfigure(int vmIndex, ArrayList<String> popNames, ArrayList<Integer> popCosts) {
    vm.configureVendingMachine(vmIndex, popNames, popCosts);
  }

  private void announceCoinLoad(int vmIndex, int coinRackIndex, Coin... coins) {
    vm.loadCoins(vmIndex, coinRackIndex, coins);
  }

  private void announcePopCanLoad(int vmIndex, int popRackIndex, PopCan... popCans) {
    vm.loadPopCans(vmIndex, popRackIndex, popCans);
  }

  private void announceUnload(int vmIndex) {
    teardown = null;
    teardown = vm.unloadVendingMachine(vmIndex);
  }

  private void announceExtract(int vmIndex) {
    extraction.clear();
    extraction.addAll(vm.extractFromDeliveryChute(vmIndex));
  }

  private void announcePress(int vmIndex, int index) {
    vm.pressButton(vmIndex, index);
  }

  private void announceInsert(int vmIndex, Coin coin) throws DisabledException {
    vm.insertCoin(vmIndex, coin);
  }

  final public boolean process(String path) throws ParseException {
  boolean res, overall = true;
  int i = 0;
        System.err.println("Script: " + path);
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CONSTRUCT:
      case CONFIGURE:
      case EXTRACT:
      case PRESS:
      case INSERT:
      case COIN_LOAD:
      case POP_LOAD:
      case UNLOAD:
      case CHECK_DELIVERY:
      case CHECK_TEARDOWN:
        ;
        break;
      default:
        break label_1;
      }
      try {
        res = Command();
      } catch (DisabledException e) {
                                                         {if (true) return false;}
      }
    overall &= res;
    System.err.print("Command #" + i++ + ": ");
    if(res)
            System.err.println("PASS");
        else
            System.err.println("FAIL");
    }
    System.err.println();
    jj_consume_token(0);
    {if (true) return overall;}
    throw new Error("Missing return statement in function");
  }

  final public boolean Command() throws ParseException, DisabledException {
  boolean res = true;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case CONSTRUCT:
      Construct();
      break;
    case CONFIGURE:
      Configure();
      break;
    case EXTRACT:
      Extract();
      break;
    case PRESS:
      Press();
      break;
    case INSERT:
      Insert();
      break;
    case COIN_LOAD:
      CoinLoad();
      break;
    case POP_LOAD:
      PopLoad();
      break;
    case UNLOAD:
      Unload();
      break;
    case CHECK_DELIVERY:
      res = CHECK_DELIVERY();
      break;
    case CHECK_TEARDOWN:
      res = CHECK_TEARDOWN();
      break;
    default:
      jj_consume_token(-1);
      throw new ParseException();
    }
  {if (true) return res;}
    throw new Error("Missing return statement in function");
  }

  final public void Construct() throws ParseException {
  Token ch;
  int selectionButtonCount, coinRackCapacity, popCanRackCapacity, receptacleCapacity;
  ArrayList<Integer> coinKinds = new ArrayList<Integer>();
    jj_consume_token(CONSTRUCT);
    jj_consume_token(LPAREN);
    ch = jj_consume_token(INTEGER_LITERAL);
    coinKinds.add(Integer.parseInt(ch.image));
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COMMA:
        ;
        break;
      default:
        break label_2;
      }
      jj_consume_token(COMMA);
      ch = jj_consume_token(INTEGER_LITERAL);
      coinKinds.add(Integer.parseInt(ch.image));
    }
    jj_consume_token(SEMICOLON);
    ch = jj_consume_token(INTEGER_LITERAL);
    selectionButtonCount = Integer.parseInt(ch.image);
    jj_consume_token(SEMICOLON);
    ch = jj_consume_token(INTEGER_LITERAL);
    coinRackCapacity = Integer.parseInt(ch.image);
    jj_consume_token(SEMICOLON);
    ch = jj_consume_token(INTEGER_LITERAL);
    popCanRackCapacity = Integer.parseInt(ch.image);
    jj_consume_token(SEMICOLON);
    ch = jj_consume_token(INTEGER_LITERAL);
    receptacleCapacity = Integer.parseInt(ch.image);
    jj_consume_token(RPAREN);
    announceConstruct(coinKinds, selectionButtonCount, coinRackCapacity, popCanRackCapacity,
            receptacleCapacity);
  }

  final public void Configure() throws ParseException {
  Token vm, name, cost;
  ArrayList<String> names = new ArrayList<String>();
  ArrayList<Integer> costs = new ArrayList<Integer>();
    jj_consume_token(CONFIGURE);
    jj_consume_token(LPAREN);
    jj_consume_token(27);
    vm = jj_consume_token(INTEGER_LITERAL);
    jj_consume_token(28);
    name = jj_consume_token(STRING_LITERAL);
    jj_consume_token(COMMA);
    cost = jj_consume_token(INTEGER_LITERAL);
    names.add(name.image);
    costs.add(Integer.parseInt(cost.image));
    label_3:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case SEMICOLON:
        ;
        break;
      default:
        break label_3;
      }
      jj_consume_token(SEMICOLON);
      name = jj_consume_token(STRING_LITERAL);
      jj_consume_token(COMMA);
      cost = jj_consume_token(INTEGER_LITERAL);
      names.add(name.image);
      costs.add(Integer.parseInt(cost.image));
    }
    jj_consume_token(RPAREN);
    announceConfigure(Integer.parseInt(vm.image), names, costs);
  }

  final public void CoinLoad() throws ParseException {
  Token vm, index, coinCount, coinValue;
    jj_consume_token(COIN_LOAD);
    jj_consume_token(LPAREN);
    jj_consume_token(27);
    vm = jj_consume_token(INTEGER_LITERAL);
    jj_consume_token(28);
    index = jj_consume_token(INTEGER_LITERAL);
    jj_consume_token(SEMICOLON);
    coinValue = jj_consume_token(INTEGER_LITERAL);
    jj_consume_token(COMMA);
    coinCount = jj_consume_token(INTEGER_LITERAL);
    jj_consume_token(RPAREN);
    int v = Integer.parseInt(coinValue.image);
    int c = Integer.parseInt(coinCount.image);
    Coin[] coins = new Coin[c];
    for(int i = 0, size = c; i < size; i++)
    {
      coins[i] = new Coin(v);
    }
    announceCoinLoad(Integer.parseInt(vm.image), Integer.parseInt(index.image), coins);
  }

  final public void PopLoad() throws ParseException {
  Token vm, index, popCount, popName;
    jj_consume_token(POP_LOAD);
    jj_consume_token(LPAREN);
    jj_consume_token(27);
    vm = jj_consume_token(INTEGER_LITERAL);
    jj_consume_token(28);
    index = jj_consume_token(INTEGER_LITERAL);
    jj_consume_token(SEMICOLON);
    popName = jj_consume_token(STRING_LITERAL);
    jj_consume_token(COMMA);
    popCount = jj_consume_token(INTEGER_LITERAL);
    jj_consume_token(RPAREN);
    int c = Integer.parseInt(popCount.image);
    PopCan[] popCans = new PopCan[c];
    for(int i = 0, size = c; i < size; i++)
    {
      popCans[i] = new PopCan(popName.image);
    }
    announcePopCanLoad(Integer.parseInt(vm.image), Integer.parseInt(index.image), popCans);
  }

  final public boolean CHECK_DELIVERY() throws ParseException {
  Token ch, pop;
  int change;
  ArrayList<String> pops = new ArrayList<String>();
    jj_consume_token(CHECK_DELIVERY);
    jj_consume_token(LPAREN);
    ch = jj_consume_token(INTEGER_LITERAL);
    change = Integer.parseInt(ch.image);
    label_4:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COMMA:
        ;
        break;
      default:
        break label_4;
      }
      jj_consume_token(COMMA);
      pop = jj_consume_token(STRING_LITERAL);
          pops.add(pop.image);
    }
    jj_consume_token(RPAREN);
    {if (true) return checkDelivery(change, pops);}
    throw new Error("Missing return statement in function");
  }

  final public boolean CHECK_TEARDOWN() throws ParseException {
  Token ch, pop;
  int change, payments;
  ArrayList<String> pops = new ArrayList<String>();
    jj_consume_token(CHECK_TEARDOWN);
    jj_consume_token(LPAREN);
    ch = jj_consume_token(INTEGER_LITERAL);
    change = Integer.parseInt(ch.image);
    jj_consume_token(SEMICOLON);
    ch = jj_consume_token(INTEGER_LITERAL);
    payments = Integer.parseInt(ch.image);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case SEMICOLON:
      jj_consume_token(SEMICOLON);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case STRING_LITERAL:
        pop = jj_consume_token(STRING_LITERAL);
          pops.add(pop.image);
        label_5:
        while (true) {
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case COMMA:
            ;
            break;
          default:
            break label_5;
          }
          jj_consume_token(COMMA);
          pop = jj_consume_token(STRING_LITERAL);
          pops.add(pop.image);
        }
        break;
      default:
        ;
      }
      break;
    default:
      ;
    }
    jj_consume_token(RPAREN);
    {if (true) return checkTeardown(change, payments, pops);}
    throw new Error("Missing return statement in function");
  }

  final public void Extract() throws ParseException {
  Token vm;
    jj_consume_token(EXTRACT);
    jj_consume_token(LPAREN);
    jj_consume_token(27);
    vm = jj_consume_token(INTEGER_LITERAL);
    jj_consume_token(28);
    jj_consume_token(RPAREN);
    announceExtract(Integer.parseInt(vm.image));
  }

  final public void Press() throws ParseException {
  Token vm, t;
    jj_consume_token(PRESS);
    jj_consume_token(LPAREN);
    jj_consume_token(27);
    vm = jj_consume_token(INTEGER_LITERAL);
    jj_consume_token(28);
    t = jj_consume_token(INTEGER_LITERAL);
    jj_consume_token(RPAREN);
    announcePress(Integer.parseInt(vm.image), Integer.parseInt(t.image));
  }

  final public void Insert() throws ParseException, DisabledException {
  Token vm, t;
    jj_consume_token(INSERT);
    jj_consume_token(LPAREN);
    jj_consume_token(27);
    vm = jj_consume_token(INTEGER_LITERAL);
    jj_consume_token(28);
    t = jj_consume_token(INTEGER_LITERAL);
    jj_consume_token(RPAREN);
    announceInsert(Integer.parseInt(vm.image), new Coin(Integer.parseInt(t.image)));
  }

  final public void Unload() throws ParseException {
  Token vm;
    jj_consume_token(UNLOAD);
    jj_consume_token(LPAREN);
    jj_consume_token(27);
    vm = jj_consume_token(INTEGER_LITERAL);
    jj_consume_token(28);
    jj_consume_token(RPAREN);
    announceUnload(Integer.parseInt(vm.image));
  }

  /** Generated Token Manager. */
  public ParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;

  /** Constructor with InputStream. */
  public Parser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public Parser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new ParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
  }

  /** Constructor. */
  public Parser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new ParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
  }

  /** Constructor with generated Token Manager. */
  public Parser(ParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
  }

  /** Reinitialise. */
  public void ReInit(ParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      return token;
    }
    token = oldToken;
    throw generateParseException();
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  /** Generate ParseException. */
  public ParseException generateParseException() {
    Token errortok = token.next;
    int line = errortok.beginLine, column = errortok.beginColumn;
    String mess = (errortok.kind == 0) ? tokenImage[0] : errortok.image;
    return new ParseException("Parse error at line " + line + ", column " + column + ".  Encountered: " + mess);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

}
