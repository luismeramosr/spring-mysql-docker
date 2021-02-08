package com.idat.proyect.web.controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import com.idat.proyect.domain.service.ClientService;
import com.idat.proyect.persistence.entity.Client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

// @CrossOrigin(origins="*")
@RestController
@RequestMapping("/client")
public class ClientController {

     @Value("${myConfig.pathImagesClients}")
     String nameDirectoryPhotos;
     
     @Autowired
     private ClientService clientService;

     @GetMapping("/all")
     @ApiOperation("Get all clients")
     @ApiResponse(code = 200, message = "OK")
     public ResponseEntity<List<Client>> getAll() {
          return new ResponseEntity<>(clientService.getAll(), HttpStatus.OK);
     }

     @GetMapping("/{id}")
     @ApiOperation("Search a client with a ID")
     @ApiResponses({ @ApiResponse(code = 200, message = "OK"),
               @ApiResponse(code = 404, message = "Product not found") })
     public ResponseEntity<Client> getById(
               @ApiParam(value = "The id of the client", required = true, example = "5") @PathVariable("id") int idClient) {
          // si no existe un producto retorna un NOT_FOUND
          return clientService.getClient(idClient).map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
     }

     @PostMapping
     @ApiOperation("Save a Client")
     @ApiResponse(code = 201, message = "OK")
     public ResponseEntity<Client> save(@RequestBody Client client) {
          client.setIdRol(1);
          return new ResponseEntity<>(clientService.save(client), HttpStatus.CREATED);
     }

     @PutMapping
     @ApiOperation("Update a Client")
     @ApiResponse(code = 201, message = "OK")
     public ResponseEntity<Client> update(@RequestBody Client client) {

          Client currentClient = clientService.getClient(client.getIdClient()).map(Client -> {
               return Client;
          }).orElse(null);
          currentClient.setAddress(client.getAddress());
          currentClient.setActive(client.getActive());
          currentClient.setPhone(client.getPhone());
          currentClient.setFirstName(client.getFirstName());
          currentClient.setLastName(client.getLastName());
          currentClient.setPassword(client.getPassword());
          return new ResponseEntity<>(clientService.save(currentClient), HttpStatus.CREATED);
     }

     @DeleteMapping("/{id}")
     @ApiOperation("Delete a Client by ID")
     @ApiResponse(code = 201, message = "OK")
     public ResponseEntity<?> delete(
               @ApiParam(value = "The id of the client", required = true, example = "1") @PathVariable("id") int idClient) {
          return (clientService.delete(idClient)) ? new ResponseEntity<>(HttpStatus.OK)
                    : new ResponseEntity<>(HttpStatus.NOT_FOUND);
     }

     // subir imagen
     @PostMapping("/photos/upload")
     public ResponseEntity<Client> uploadPhotoClient(@RequestParam("imgFile") MultipartFile file,
               @RequestParam("idClient") Integer idClient) {

          Client cliente = clientService.getClient(idClient).map(client -> {
               return client;
          }).orElse(null);
          // si el archivo no esta vacio y si existe un cliente
          if (!file.isEmpty() && cliente != null) {
               // genera identificador unico
               String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename().replace(" ", "");
               Path filePath = Paths.get(this.nameDirectoryPhotos).resolve(fileName).toAbsolutePath();
               // log.info(filePath.toString());
               try {
                    // copio el archivo a la ruta especificada
                    Files.copy(file.getInputStream(), filePath);
               } catch (IOException e) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
               }
               // antes de guardar eliminamos la foto existente
               this.removePhotoClient(idClient);
               // guardo nueva foto
               cliente.setProfilePictureUrl(fileName);
               var clienteUpdated = clientService.savePhoto(cliente);
               return new ResponseEntity<Client>(clienteUpdated, HttpStatus.ACCEPTED);
          }
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

     }

     // metodo para eliminar foto de client si existira otra
     public void removePhotoClient(Integer id) {
          Client cliente = clientService.getClient(id).map(client -> client).orElse(null);
          String nombreFotoAnterior = cliente.getProfilePictureUrl();

          if (nombreFotoAnterior != null && nombreFotoAnterior.length() > 0) {
               Path rutaFotoAnterior = Paths.get(this.nameDirectoryPhotos).resolve(nombreFotoAnterior).toAbsolutePath();
               File archivoAnterior = rutaFotoAnterior.toFile();
               if (archivoAnterior.exists() && archivoAnterior.canRead()) {
                    archivoAnterior.delete();
               }
          }
     }
}