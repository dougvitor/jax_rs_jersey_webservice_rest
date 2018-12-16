package br.com.alura.loja.resource;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.com.alura.loja.dao.CarrinhoDAO;
import br.com.alura.loja.modelo.Carrinho;
import br.com.alura.loja.modelo.Produto;

@Path("carrinhos")
public class CarrinhoResource {

	@Path("{id}")
	@GET
    @Produces(MediaType.APPLICATION_XML)
    public Carrinho busca(@PathParam("id") long id) {
        Carrinho carrinho = new CarrinhoDAO().busca(id);
        return carrinho;
    }
	
	@POST
    @Consumes(MediaType.APPLICATION_XML)
    public Response adiciona(Carrinho carrinho) {
        new CarrinhoDAO().adiciona(carrinho);
        URI uri = URI.create("/carrinhos/" + carrinho.getId());
        return Response.created(uri).build();
    }
	
	@Path("{carrinhoId}/produtos/{produtoId}")
    @DELETE
    public Response removeProduto(@PathParam("carrinhoId") long carrinhoId, @PathParam("produtoId") long produtoId) {
		Carrinho carrinho = new CarrinhoDAO().busca(carrinhoId);
		carrinho.remove(produtoId);
		return Response.ok().build();
    }
	
	@Path("{carrinhoId}/produtos/{produtoId}/quantidade")
	@PUT
	public Response alteraProduto(@PathParam("carrinhoId") long carrinhoId, @PathParam("produtoId") long produtoId, Produto produto) {
		Carrinho carrinho = new CarrinhoDAO().busca(carrinhoId);
        carrinho.trocaQuantidade(produto);
		return Response.ok().build();
	}

}