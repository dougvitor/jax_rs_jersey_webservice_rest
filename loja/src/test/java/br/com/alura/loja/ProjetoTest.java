package br.com.alura.loja;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.alura.loja.modelo.Projeto;
import junit.framework.Assert;

public class ProjetoTest {
	
	private HttpServer server;
	   
   @Before
	public void iniciarServidor() {
	   ResourceConfig config = new ResourceConfig().packages("br.com.alura.loja");
       URI uri = URI.create("http://localhost:8080/");
       this.server = GrizzlyHttpServerFactory.createHttpServer(uri, config);
	}

	@Test
    public void testaQueAConexaoComOServidorFuncionaNoPathDeProjetos() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080");
        String conteudo = target.path("/projetos/1").request().get(String.class);
        Assert.assertTrue(conteudo.contains("<nome>Minha loja"));
    }
	
	@Test
    public void testaQueBuscarUmProjetoTrazOProjetoEsperado() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080");
        Projeto projeto = target.path("/projetos/1").request().get(Projeto.class);
        Assert.assertEquals("Minha loja", projeto.getNome());
    }
	
	@Test
	public void adiciona() {
		Projeto projeto =  new Projeto(1l, "Contmatic", 2018);
		
		Entity<Projeto> entity = Entity.entity(projeto, MediaType.APPLICATION_XML);
		 
		Client client = ClientBuilder.newClient();
	    WebTarget target = client.target("http://localhost:8080");
	    
        Response response = target.path("/projetos").request().post(entity);
        Assert.assertEquals(201, response.getStatus());
        
        String location = response.getHeaderString("Location");
        Projeto projetoCarregado = client.target(location).request().get(Projeto.class);
        
        Assert.assertEquals("Contmatic", projetoCarregado.getNome());
	}
	
	@After
	public void pararServidor() {
		this.server.stop();
	}
}