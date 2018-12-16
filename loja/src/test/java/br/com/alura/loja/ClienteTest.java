package br.com.alura.loja;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import br.com.alura.loja.modelo.Carrinho;
import br.com.alura.loja.modelo.Produto;
import junit.framework.Assert;

public class ClienteTest {

   private HttpServer server;
   private Client client;
   
   @Before
	public void iniciarServidor() {
	   ResourceConfig config = new ResourceConfig().packages("br.com.alura.loja");
       URI uri = URI.create("http://localhost:8080/");
       this.server = GrizzlyHttpServerFactory.createHttpServer(uri, config);
       
       ClientConfig conf = new ClientConfig();
       conf.register(new LoggingFilter());
       client = ClientBuilder.newClient(conf);
	}

   @Test
    public void testaQueAConexaoComOServidorFunciona() {
     
        WebTarget target = client.target("http://www.mocky.io");
        String conteudo = target.path("/v2/52aaf5deee7ba8c70329fb7d").request().get(String.class);
        Assert.assertTrue(conteudo.contains("<rua>Rua Vergueiro 3185"));
    }
    
    @Test
    public void testaQueBuscarUmCarrinhoTrazOCarrinhoEsperado() {
        WebTarget target = client.target("http://localhost:8080");
        Carrinho carrinho = target.path("/carrinhos/1").request().get(Carrinho.class);
        Assert.assertEquals("Videogame 4", carrinho.getProdutos().get(0).getNome());
    }
    
    @Test
    public void adicionaCarinho() {
    	Carrinho carrinho = new Carrinho();
        carrinho.adiciona(new Produto(314L, "Tablet", 999, 1));
        carrinho.setRua("Rua Vergueiro");
        carrinho.setCidade("Sao Paulo");
        
        Entity<Carrinho> entity = Entity.entity(carrinho, MediaType.APPLICATION_XML);

        WebTarget target = client.target("http://localhost:8080");
        Response response = target.path("/carrinhos").request().post(entity);
        Assert.assertEquals(201, response.getStatus());
        
        String location = response.getHeaderString("Location");
        Carrinho carrinhoCarregado = client.target(location).request().get(Carrinho.class);
        Assert.assertEquals("Tablet", carrinhoCarregado.getProdutos().get(0).getNome());
    }
    
    @Test
    public void atualizarProduto() {
    	Carrinho carrinho = new Carrinho();
    	carrinho.setId(1L);
    	Produto produto = new Produto();
    	produto.setId(1L);
    	produto.setQuantidade(10);
        carrinho.adiciona(produto);
        
        Entity<Produto> entity = Entity.entity(produto, MediaType.APPLICATION_XML);

        WebTarget target = client.target("http://localhost:8080");
        Response response = target.path("/carrinhos/" + carrinho.getId() + "/produtos/" + produto.getId() + "/quantidade").request().put(entity);
        
        Assert.assertEquals(200, response.getStatus());
    }

    @After
	public void pararServidor() {
		this.server.stop();
	}
}