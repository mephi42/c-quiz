package org.mephi.cquiz

import scala.sys.process._

import util.Random
import java.io.ByteArrayInputStream

object Expressions extends Topic {
  override val id = "eval-cond"

  override val title = "Выражения"

  override val description = "Тест на знание выражений"

  override def nextQuestion() = new Question {
    def write(writer: Writer) {
      for (line <- (Process(Seq("perl", "--", "-", seed.toString)) #< new ByteArrayInputStream(perlScript)).lines) {
        writer.write(line).nextLine()
      }
    }

    private val seed = new Random().nextInt()
  }

  private val perlScript =
    """
      |#!/usr/bin/perl -w
      |use strict;
      |use warnings;
      |use autodie qw( :all );
      |
      |my $seed = shift;
      |if ( defined $seed ) { srand( $seed ); }
      |
      |my %vars = ();
      |my $depth = 2;
      |sub expr {
      |  my @choices = do { if ( $depth == 0 ) {
      |    ( \&val )
      |  } else {
      |    ( \&ternary, \&comma, \&assign, \&eq, \&neq, \&gt, \&lt, \&geq, \&leq, \&and, \&or )
      |  } };
      |  my $fun = $choices[ int( rand( scalar @choices ) ) ];
      |  $depth--;
      |  my $result = $fun->();
      |  $depth++;
      |  return $result;
      |}
      |
      |sub var {
      |  my $vars = 'abcdefghijklmnopqrstuvwxyz';
      |  my $var = substr( $vars, int( rand( length( $vars ) ) ), 1 );
      |  $vars{$var} = 1;
      |  return $var;
      |}
      |
      |sub val {
      |  if ( rand( 3 ) < 1 ) {
      |    return 0;
      |  }
      |  return int( rand( 20 ) );
      |}
      |sub ternary { return '(' . expr() . ' ? ' . expr() . ' : ' . expr() . ')'; }
      |sub comma { return '(' . expr() . ', ' . expr() . ')'; }
      |sub assign { return '(' . var() . ' = ' . expr() . ')'; }
      |sub eq { return '(' . expr() . ' == ' . expr() . ')'; }
      |sub neq { return '(' . expr() . ' != ' . expr() . ')'; }
      |sub gt { return '(' . expr() . ' > ' . expr() . ')'; }
      |sub lt { return '(' . expr() . ' < ' . expr() . ')'; }
      |sub geq { return '(' . expr() . ' >= ' . expr() . ')'; }
      |sub leq { return '(' . expr() . ' <= ' . expr() . ')'; }
      |sub and { return '(' . expr() . ' && ' . expr() . ')'; }
      |sub or { return '(' . expr() . ' || ' . expr() . ')'; }
      |sub div { return '(' . expr() . ' / ' . expr() . ')'; }
      |sub mod { return '(' . expr() . ' % ' . expr() . ')'; }
      |
      |my $expr = expr();
      |if ( %vars ) {
      |  print 'int ' . join( ', ', sort keys %vars ) . ";\n";
      |}
      |print 'printf( "%i\n", ' . $expr . ");\n";
    """.stripMargin.getBytes
}
