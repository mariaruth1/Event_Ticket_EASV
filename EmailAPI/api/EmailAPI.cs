using SendGrid;
using SendGrid.Helpers.Mail;
using System;
using System.IO;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Azure.WebJobs;
using Microsoft.Azure.WebJobs.Extensions.Http;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json;
using System.Security.Cryptography;

namespace EventTicketSystem.API
{
    public static class EmailAPI
    {
        [FunctionName("EmailAPI")]
        public static async Task<IActionResult> Run(
            [HttpTrigger(AuthorizationLevel.Anonymous, "get", "post", Route = null)] HttpRequest req,
            ILogger log)
        {
            var apiKey = "<Your-Sendgrid-Key>";
            var client = new SendGridClient(apiKey);
            var msg = new SendGridMessage()
            {
                From = new EmailAddress("matejmazur@outlook.com", "EASV Tickets"),
                Subject = $"Your ticket for {req.Form["event"]}",
                PlainTextContent = $"Hi, this is your ticket for the event {req.Form["event"]}."
            };
            msg.AddTo(new EmailAddress($"{req.Form["ToEmail"]}", $"{req.Form["ToName"]}"));

            var formCollection = await req.ReadFormAsync();
            var fileContent = formCollection.Files["file"];

            using (var fileStream = fileContent.OpenReadStream())
            {
                await msg.AddAttachmentAsync(fileContent.FileName, fileStream);
                var response = await client.SendEmailAsync(msg);
            }

            return new OkObjectResult("Email has been sent");
        } 
    }
}
