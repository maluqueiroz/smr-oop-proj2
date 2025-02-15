/**********************************
 * IFPB - Curso Superior de Tec. em Sist. para Internet
 * POO - Programa��o Orientada a Objetos
 * Prof. Fausto Ayres
 *
 */
package fachada;
import java.util.ArrayList;

import modelo.*;
import repositorio.Repositorio;
import javax.swing.ImageIcon;

public class Fachada {
	private static Repositorio repositorio = new Repositorio();
	private static Pessoa logado;
	private static int idMensagem=0;	//autoincremento

	public static Pessoa login(String email, String senha) throws  Exception {
		if(logado != null) {
			throw new Exception("Um usuario j� est� logado: " + logado.getEmail());
		}

		Pessoa usuario = repositorio.localizarUsuario(email,senha);
		if( usuario == null ) {
			throw new Exception("Email ou senha inv�lidos");
		}

		logado = usuario;
		return usuario;
	}
	public static void logoff() throws  Exception {
		if( logado == null ) {
			throw new Exception("Nenhum usuario est� logado no momento");
		}
		logado = null;
	}
	public static Pessoa getLogado() {
		return logado;
	}

	public static Pessoa cadastrarUsuario(
			String email,
			String senha,
			String nome,
			ImageIcon imagem)
			throws  Exception{

		Pessoa usuario = repositorio.localizarUsuario(email,senha);
		if ( usuario != null ) {
			throw new Exception("Usu�rio " + email + " j� cadastrado");
		}

		usuario = new Pessoa(email, senha, nome, imagem);

		repositorio.adicionarUsuario(usuario);

		return usuario;
	}
	
	public static Pessoa cadastrarUsuario(
			String email,
			String senha,
			String nome)
			throws  Exception{

		Pessoa usuario = repositorio.localizarUsuario(email,senha);
		if ( usuario != null ) {
			throw new Exception("Usu�rio " + email + " j� cadastrado");
		}

		usuario = new Pessoa(email, senha, nome);

		repositorio.adicionarUsuario(usuario);

		return usuario;
	}
	
	public static Pessoa cadastrarAdministrador(
			String email,
			String senha,
			String nome,
			ImageIcon imagem,
			String setor)
			throws  Exception{

		Pessoa admin = repositorio.localizarUsuario(email,senha);
		if ( admin != null ) {
			throw new Exception("Usu�rio " + email + " j� cadastrado");
		}

		admin = new Administrador(email, senha, nome, imagem, setor);

		repositorio.adicionarUsuario(admin);

		return admin;
	}
	
	public static Administrador cadastrarAdministrador(
			String email,
			String senha,
			String nome,
			String setor)
			throws  Exception{

		Administrador admin = (Administrador) repositorio.localizarUsuario(email,senha);
		
		if ( admin != null ) {
			throw new Exception("Usu�rio " + email + " j� cadastrado");
		}

		admin = new Administrador(email, senha, nome, setor);

		repositorio.adicionarUsuario(admin);

		return admin;
	}

	public static ArrayList<Pessoa> listarPessoas(String termoBusca) throws Exception {
		ArrayList<Pessoa> retorno= new ArrayList<Pessoa>();

		for (Pessoa pessoa : repositorio.getPessoas()) {
			if (pessoa.getNome().contains(termoBusca)) {
				retorno.add(pessoa);
			}
		}

		if (retorno.size()==0)
			throw new Exception("N�o encontramos usu�rio com o seguinte termo de busca: "+termoBusca);

		return retorno;
	}

	public static ArrayList<Pessoa> listarPessoas() {
		return repositorio.getPessoas();
	}

	public static Mensagem enviarMensagem (String emailDestinatario, String textoMensagem) throws Exception {

		if (logado == null)
			throw new Exception("Voc� precisa estar logado para enviar mensagem");
		
		Pessoa destinatario = repositorio.localizarUsuario(emailDestinatario);
		
		if (destinatario == null)
			throw new Exception("Destinatário inválido, tente novamente");
			
		idMensagem++;
		Mensagem msg = new Mensagem(idMensagem, logado, destinatario, textoMensagem);

		logado.addMensagemSaida(msg);
		destinatario.addMensagemEntrada(msg);
		repositorio.adicionarMensagem(msg);
		return msg;
	}

	public static ArrayList<Mensagem> listarCaixaEntrada() throws Exception{
		if (logado == null)
			throw new Exception("Voc� precisa estar logado para listar mensagens");
		if (logado.getMensagensEntrada().size()==0)
			throw new Exception("Voc� não tem mensagens na caixa de entrada");

		return logado.getMensagensEntrada();
	}

	public static ArrayList<Mensagem> listarCaixaSaida() throws Exception {
		if (logado == null)
			throw new Exception("Voc� precisa estar logado para listar mensagens");
		if (logado.getMensagensSaida().size()==0)
			throw new Exception("Voc� não tem mensagens na caixa de saída");

		return logado.getMensagensSaida();
	}


	public static Mensagem apagarMensagem(int idMensagem) throws Exception  {
		Mensagem remover = repositorio.localizarMensagem(idMensagem);

		if (remover == null || !remover.getEmitente().getNome().equals(logado.getNome()) && !remover.getDestinatario().getNome().equals(logado.getNome()))
			throw new Exception("Nenhuma mensagem encontrada com esse id");

		remover.getEmitente().removerMensagemSaida(remover);
		remover.getDestinatario().removerMensagemEntrada(remover);
		return remover;
	}

	public static ArrayList<Mensagem> espionarMensagens() throws Exception {
		if (logado.getClass() != Administrador.class){
			throw new Exception("Somente o administrador tem acesso a essa função");
		}
		if (repositorio.getMensagens().size()==0)
			throw new Exception("Não existem mensagens registradas");

		return repositorio.getMensagens();
	}

	public static ArrayList<Mensagem> espionarMensagens(String termoBusca) throws Exception {
		if (logado.getClass() != Administrador.class){
			throw new Exception("Somente o administrador tem acesso a essa função");
		}
		if (repositorio.getMensagens().size()==0)
			throw new Exception("Não existem mensagens registradas");


		ArrayList<Mensagem> msgs = new ArrayList<Mensagem>();
		for (Mensagem msg : repositorio.getMensagens()) {
			if (msg.getTexto().contains(termoBusca)) {
				msgs.add(msg);
			}
		}
		return msgs;
	}

	public static ArrayList<Pessoa> relatorio1() throws Exception {
		if (logado == null)
			throw new Exception("Você precisa estar logado");
		
		if (logado.getClass() != Administrador.class){
			throw new Exception("Somente o administrador tem acesso a essa função");
		}

		ArrayList<Pessoa> pessoas = new ArrayList<Pessoa>();
		for (Pessoa pessoa : repositorio.getPessoas()) {
			if (pessoa.getMensagensSaida().size()==0){
				pessoas.add(pessoa);
			}
		}
		return pessoas;
	}

	public static ArrayList<Mensagem> relatorio2()  throws Exception {
		if (logado == null)
			throw new Exception("Você precisa estar logado");
			
		if (logado.getClass() != Administrador.class){
			throw new Exception("Somente o administrador tem acesso a essa função");
		}

		ArrayList<Mensagem> msgs = new ArrayList<Mensagem>();
		for (Mensagem msg : repositorio.getMensagens()) {
			if (repositorio.localizarUsuario(msg.getEmitente().getEmail()+msg.getEmitente().getSenha())
					== repositorio.localizarUsuario(msg.getEmitente().getEmail()+msg.getEmitente().getSenha())){
				msgs.add(msg);
			}
		}
		return msgs;
	}
}
