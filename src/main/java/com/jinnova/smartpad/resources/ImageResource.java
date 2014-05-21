package com.jinnova.smartpad.resources;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.jinnova.smartpad.partner.PartnerManager;

@Path("/images")
public class ImageResource {
	
	@Path("/{typeName}/{entityId}/{imageId}")
	@Produces("image/png")
    @GET
	public Response getFullImage(@PathParam("typeName") String typeName, @PathParam("entityId") String entityId, 
			@PathParam("imageId") String imageId, @QueryParam("size") int size) throws IOException {

	    BufferedImage image = PartnerManager.instance.getImage(typeName, null, entityId, imageId, size);
	    if (image == null) {
	    	return null;
	    }

	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ImageIO.write(image, "png", baos);
	    byte[] imageData = baos.toByteArray();

	    // uncomment line below to send non-streamed
	    // return Response.ok(imageData).build();

	    // uncomment line below to send streamed
	    return Response.ok(new ByteArrayInputStream(imageData)).build();
	}
}
